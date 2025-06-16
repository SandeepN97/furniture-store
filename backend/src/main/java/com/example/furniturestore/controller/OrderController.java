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

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.OrderItem;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.UserRepository;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public static class ItemRequest {
        public Long productId;
        public int quantity;
    }

    public static class CreateOrderRequest {
        public String customerName;
        public List<ItemRequest> items;
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
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            order.setUser(user);
        }
        for (ItemRequest ir : request.items) {
            Product product = productRepository.findById(ir.productId).orElse(null);
            if (product == null || product.getStockQuantity() < ir.quantity) {
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
        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }
}
