package com.example.furniturestore.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Category;
import com.example.furniturestore.model.Order;
import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.OrderRepository;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.UserRepository;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public RecommendationController(OrderRepository orderRepository, ProductRepository productRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<List<Product>> recommended(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        Order last = orderRepository.findTopByUserOrderByOrderDateDesc(user);
        if (last == null || last.getItems().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        Category category = last.getItems().get(0).getProduct().getCategory();
        if (category == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<Product> products = productRepository
                .findByCategoryId(category.getId(), PageRequest.of(0, 5))
                .getContent();
        return ResponseEntity.ok(products);
    }
}
