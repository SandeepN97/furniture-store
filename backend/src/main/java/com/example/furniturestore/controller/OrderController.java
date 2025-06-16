package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.OrderItem;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.UserRepository;
import com.example.furniturestore.service.EmailService;
import com.example.furniturestore.service.InvoiceService;
import com.example.furniturestore.service.OrderNotificationService;
import com.example.furniturestore.model.OrderStatus;
import com.example.furniturestore.model.Coupon;
import com.example.furniturestore.repository.CouponRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final InvoiceService invoiceService;
    private final OrderNotificationService notificationService;
    private final CouponRepository couponRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository,
            UserRepository userRepository, EmailService emailService, InvoiceService invoiceService,
            OrderNotificationService notificationService, CouponRepository couponRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.invoiceService = invoiceService;
        this.notificationService = notificationService;
        this.couponRepository = couponRepository;
    }

    public static class ItemRequest {
        public Long productId;
        public int quantity;
    }

    public static class CreateOrderRequest {
        public String customerName;
        public List<ItemRequest> items;
        public String couponCode;
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> getInvoice(@PathVariable Long id, Authentication authentication) throws java.io.IOException {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        if (order.getUser() != null && authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            if (!email.equals(order.getUser().getEmail()) && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(403).build();
            }
        }
        byte[] pdf = invoiceService.generateInvoice(order);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderRequest request,
            Authentication authentication) {
        if (request == null || request.items == null || request.items.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Order order = new Order();
        order.setCustomerName(request.customerName);
        order.setStatus(OrderStatus.PENDING);
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            order.setUser(user);
        }
        for (ItemRequest ir : request.items) {
            Product product = productRepository.findById(ir.productId).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().build();
            }
            if (product.getStockQuantity() < ir.quantity || product.getStockQuantity() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            OrderItem item = new OrderItem(product, ir.quantity, product.getPrice());
            item.setOrder(order);
            order.getItems().add(item);
            product.setStockQuantity(product.getStockQuantity() - ir.quantity);
            productRepository.save(product);
        }
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.couponCode != null) {
            Coupon coupon = couponRepository.findByCode(request.couponCode).orElse(null);
            if (coupon != null) {
                BigDecimal discount = total.multiply(BigDecimal.valueOf(coupon.getDiscountPercent()).divide(BigDecimal.valueOf(100)));
                total = total.subtract(discount);
            }
        }
        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        notificationService.notifyNewOrder(saved);
        if (saved.getUser() != null) {
            emailService.sendOrderConfirmation(saved.getUser(), saved);
        }
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/guest")
    public ResponseEntity<Order> createGuest(@RequestBody CreateOrderRequest request) {
        return create(request, null);
    }

    public static class UpdateStatusRequest {
        public String status;
    }

    @PostMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            order.setStatus(OrderStatus.valueOf(request.status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }
}
