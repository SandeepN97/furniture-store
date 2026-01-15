package com.example.furniturestore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.Product;
import com.example.furniturestore.model.User;
import com.example.furniturestore.model.Wishlist;
import com.example.furniturestore.repository.ProductRepository;
import com.example.furniturestore.repository.WishlistRepository;
import com.example.furniturestore.security.UserPrincipal;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public WishlistController(WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<?> getUserWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        List<Wishlist> wishlist = wishlistRepository.findByUserId(userPrincipal.getId());
        return ResponseEntity.ok(wishlist);
    }

    public static class AddToWishlistRequest {
        public Long productId;
    }

    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody AddToWishlistRequest request,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userPrincipal.getId(), request.productId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product already in wishlist"));
        }

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = new User();
        user.setId(userPrincipal.getId());

        Wishlist wishlist = new Wishlist(user, product);
        Wishlist saved = wishlistRepository.save(wishlist);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        wishlistRepository.deleteByUserIdAndProductId(userPrincipal.getId(), productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkInWishlist(@PathVariable Long productId,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.ok(Map.of("inWishlist", false));
        }

        boolean inWishlist = wishlistRepository.existsByUserIdAndProductId(userPrincipal.getId(), productId);
        return ResponseEntity.ok(Map.of("inWishlist", inWishlist));
    }
}
