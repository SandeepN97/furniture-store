package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.UserRepository;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public AdminController(OrderRepository orderRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/orders")
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @GetMapping("/orders/status/{status}")
    public List<Order> getOrdersByStatus(@PathVariable String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findByStatus(orderStatus);
    }

    public static class UpdateOrderStatusRequest {
        public String status;
        public String trackingNumber;
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id,
                                               @RequestBody UpdateOrderStatusRequest request) {
        return orderRepository.findById(id).map(order -> {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(request.status.toUpperCase());
            order.setStatus(newStatus);

            if (request.trackingNumber != null) {
                order.setTrackingNumber(request.trackingNumber);
            }

            if (newStatus == Order.OrderStatus.SHIPPED && order.getShippedAt() == null) {
                order.setShippedAt(LocalDateTime.now());
            }

            if (newStatus == Order.OrderStatus.DELIVERED && order.getDeliveredAt() == null) {
                order.setDeliveredAt(LocalDateTime.now());
            }

            Order updated = orderRepository.save(order);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Total orders
        long totalOrders = orderRepository.count();
        analytics.put("totalOrders", totalOrders);

        // Total revenue
        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() != Order.OrderStatus.CANCELLED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        analytics.put("totalRevenue", totalRevenue);

        // Total products
        long totalProducts = productRepository.count();
        analytics.put("totalProducts", totalProducts);

        // Total users
        long totalUsers = userRepository.count();
        analytics.put("totalUsers", totalUsers);

        // Low stock products count
        long lowStockCount = productRepository.findLowStockProducts().size();
        analytics.put("lowStockCount", lowStockCount);

        // Orders by status
        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.findByStatus(status).size();
            ordersByStatus.put(status.name(), count);
        }
        analytics.put("ordersByStatus", ordersByStatus);

        // Recent orders (last 10)
        List<Order> recentOrders = orderRepository.findTop10ByOrderByOrderDateDesc();
        analytics.put("recentOrders", recentOrders);

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyRevenue() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Object[]> results = orderRepository.getMonthlyRevenue(oneYearAgo);

        List<Map<String, Object>> monthlyData = results.stream()
                .map(row -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("month", row[0]);
                    data.put("year", row[1]);
                    data.put("revenue", row[2]);
                    data.put("orderCount", row[3]);
                    return data;
                })
                .toList();

        return ResponseEntity.ok(monthlyData);
    }
}
