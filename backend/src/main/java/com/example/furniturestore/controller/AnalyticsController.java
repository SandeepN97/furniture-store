package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.repository.OrderItemRepository;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public AnalyticsController(OrderRepository orderRepository, UserRepository userRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public static class TopProduct {
        public String name;
        public long quantity;

        public TopProduct(String name, long quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    public static class AnalyticsResponse {
        public long totalUsers;
        public long totalOrders;
        public BigDecimal totalRevenue;
        public List<TopProduct> topProducts;

        public AnalyticsResponse(long totalUsers, long totalOrders, BigDecimal totalRevenue, List<TopProduct> topProducts) {
            this.totalUsers = totalUsers;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.topProducts = topProducts;
        }
    }

    @GetMapping
    public AnalyticsResponse getAnalytics() {
        long users = userRepository.count();
        long orders = orderRepository.count();
        BigDecimal revenue = orderRepository.sumTotalPrice();
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        var projections = orderItemRepository.findTopProducts(PageRequest.of(0, 5));
        List<TopProduct> top = projections.stream()
                .map(p -> new TopProduct(p.getName(), p.getQuantity()))
                .toList();
        return new AnalyticsResponse(users, orders, revenue, top);
    }
}
