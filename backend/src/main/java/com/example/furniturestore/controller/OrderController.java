package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.*;
import com.example.furniturestore.repository.*;
import com.example.furniturestore.service.EmailService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final CouponRepository couponRepository;
    private final EmailService emailService;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository,
            UserRepository userRepository, ShippingAddressRepository shippingAddressRepository,
            CouponRepository couponRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.couponRepository = couponRepository;
        this.emailService = emailService;
    }

    public static class ItemRequest {
        public Long productId;
        public int quantity;
    }

    public static class CreateOrderRequest {
        public String customerName;
        public List<ItemRequest> items;
        public Long shippingAddressId;
        public String couponCode;
        public String paymentMethod;
        public String paymentIntentId;

        // Guest checkout fields
        public String shippingFullName;
        public String shippingAddressLine1;
        public String shippingAddressLine2;
        public String shippingCity;
        public String shippingState;
        public String shippingZipCode;
        public String shippingCountry;
        public String shippingPhoneNumber;
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

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody CreateOrderRequest request,
            Authentication authentication) {
        if (request == null || request.items == null || request.items.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Order order = new Order();
        order.setCustomerName(request.customerName);
        order.setPaymentMethod(request.paymentMethod);
        order.setPaymentIntentId(request.paymentIntentId);

        User user = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            user = userRepository.findByEmail(email).orElse(null);
            order.setUser(user);
        }

        // Handle shipping address
        if (request.shippingAddressId != null && user != null) {
            ShippingAddress address = shippingAddressRepository.findById(request.shippingAddressId)
                    .orElse(null);
            if (address != null) {
                order.setShippingAddress(address);
                order.setShippingFullName(address.getFullName());
                order.setShippingAddressLine1(address.getAddressLine1());
                order.setShippingAddressLine2(address.getAddressLine2());
                order.setShippingCity(address.getCity());
                order.setShippingState(address.getState());
                order.setShippingZipCode(address.getZipCode());
                order.setShippingCountry(address.getCountry());
                order.setShippingPhoneNumber(address.getPhoneNumber());
            }
        } else {
            // Guest checkout or manual address
            order.setShippingFullName(request.shippingFullName);
            order.setShippingAddressLine1(request.shippingAddressLine1);
            order.setShippingAddressLine2(request.shippingAddressLine2);
            order.setShippingCity(request.shippingCity);
            order.setShippingState(request.shippingState);
            order.setShippingZipCode(request.shippingZipCode);
            order.setShippingCountry(request.shippingCountry);
            order.setShippingPhoneNumber(request.shippingPhoneNumber);
        }

        // Add order items and check stock
        for (ItemRequest ir : request.items) {
            Product product = productRepository.findById(ir.productId).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().build();
            }

            // Check stock
            if (product.getStockQuantity() != null && product.getStockQuantity() < ir.quantity) {
                return ResponseEntity.badRequest().build(); // Insufficient stock
            }

            OrderItem item = new OrderItem(product, ir.quantity, product.getPrice());
            item.setOrder(order);
            order.getItems().add(item);

            // Reduce stock
            if (product.getStockQuantity() != null) {
                product.setStockQuantity(product.getStockQuantity() - ir.quantity);
                productRepository.save(product);
            }
        }

        // Calculate subtotal
        BigDecimal subtotal = order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setSubtotal(subtotal);

        // Calculate shipping cost (simple flat rate or free shipping over $100)
        BigDecimal shippingCost = subtotal.compareTo(new BigDecimal("100")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("10.00");
        order.setShippingCost(shippingCost);

        // Calculate tax (8% example)
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08"))
                .setScale(2, RoundingMode.HALF_UP);
        order.setTax(tax);

        // Apply coupon if provided
        BigDecimal discount = BigDecimal.ZERO;
        if (request.couponCode != null && !request.couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCodeIgnoreCase(request.couponCode)
                    .orElse(null);
            if (coupon != null && coupon.isValid()) {
                if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                    discount = subtotal.multiply(coupon.getDiscountValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                } else {
                    discount = coupon.getDiscountValue();
                }
                order.setCouponCode(request.couponCode);
                coupon.setUsageCount(coupon.getUsageCount() + 1);
                couponRepository.save(coupon);
            }
        }
        order.setDiscount(discount);

        // Calculate total
        BigDecimal total = subtotal.add(shippingCost).add(tax).subtract(discount);
        order.setTotalPrice(total);

        order.setStatus(Order.OrderStatus.PENDING);
        Order saved = orderRepository.save(order);

        // Send order confirmation email
        if (user != null) {
            emailService.sendOrderConfirmation(saved, user);
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        return orderRepository.findById(id).map(order -> {
            if (user != null && order.getUser() != null &&
                !order.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).<Order>build();
            }

            if (order.getStatus() == Order.OrderStatus.SHIPPED ||
                order.getStatus() == Order.OrderStatus.DELIVERED) {
                return ResponseEntity.badRequest().<Order>build();
            }

            order.setStatus(Order.OrderStatus.CANCELLED);

            // Restore stock
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product.getStockQuantity() != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    productRepository.save(product);
                }
            }

            Order updated = orderRepository.save(order);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}
