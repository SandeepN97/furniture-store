package com.example.furniturestore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Subscription;
import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.SubscriptionRepository;
import com.example.furniturestore.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.SubscriptionCreateParams;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secret}")
    private String stripeSecret;

    public SubscriptionController(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecret;
    }

    public static class CreateRequest {
        public String priceId;
    }

    @PostMapping
    public ResponseEntity<Subscription> create(@RequestBody CreateRequest req, Authentication authentication) throws StripeException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        SubscriptionCreateParams params = SubscriptionCreateParams.builder()
                .setCustomer(user.getId().toString())
                .addItem(SubscriptionCreateParams.Item.builder().setPrice(req.priceId).build())
                .build();
        com.stripe.model.Subscription stripeSub = com.stripe.model.Subscription.create(params);
        Subscription sub = new Subscription(user, stripeSub.getId(), stripeSub.getStatus());
        subscriptionRepository.save(sub);
        return ResponseEntity.ok(sub);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Subscription>> mySubscriptions(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(subscriptionRepository.findByUser(user));
    }
}
