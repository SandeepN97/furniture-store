package com.example.furniturestore.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.Review;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.ReviewRepository;
import com.example.furniturestore.security.UserPrincipal;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    public static class CreateReviewRequest {
        public Long productId;
        public Integer rating;
        public String comment;
    }

    @GetMapping("/product/{productId}")
    public Page<Review> getProductReviews(@PathVariable Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        // Validate rating
        if (request.rating < 1 || request.rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "Rating must be between 1 and 5"));
        }

        // Check if user already reviewed this product
        if (reviewRepository.findByProductIdAndUserId(request.productId, userPrincipal.getId()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You have already reviewed this product"));
        }

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = new User();
        user.setId(userPrincipal.getId());

        Review review = new Review(product, user, request.rating, request.comment);
        Review saved = reviewRepository.save(review);

        // Update product rating
        updateProductRating(request.productId);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,
                                         @RequestBody CreateReviewRequest request,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        return reviewRepository.findById(id).map(review -> {
            if (!review.getUser().getId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
            }

            if (request.rating < 1 || request.rating > 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rating must be between 1 and 5"));
            }

            review.setRating(request.rating);
            review.setComment(request.comment);
            Review updated = reviewRepository.save(review);

            // Update product rating
            updateProductRating(review.getProduct().getId());

            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        return reviewRepository.findById(id).map(review -> {
            if (!review.getUser().getId().equals(userPrincipal.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Not authorized"));
            }

            Long productId = review.getProduct().getId();
            reviewRepository.delete(review);

            // Update product rating
            updateProductRating(productId);

            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            Double avgRating = reviewRepository.calculateAverageRating(productId);
            long reviewCount = reviewRepository.countByProductId(productId);

            product.setAverageRating(avgRating != null ? avgRating : 0.0);
            product.setReviewCount((int) reviewCount);
            productRepository.save(product);
        }
    }
}
