package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Product;
import com.example.furniturestore.repository.ProductRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final ProductRepository productRepository;

    @Value("${stripe.secret}")
    private String stripeSecret;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public PaymentController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecret;
    }

    public static class Item {
        public Long productId;
        public int quantity;
    }

    public static class CreateSessionRequest {
        public List<Item> items;
    }

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody CreateSessionRequest request) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (Item item : request.items) {
            Product product = productRepository.findById(item.productId).orElseThrow();
            long amount = product.getPrice().multiply(BigDecimal.valueOf(100)).longValue();
            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd")
                    .setUnitAmount(amount)
                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(product.getName())
                            .build())
                    .build();
            lineItems.add(SessionCreateParams.LineItem.builder()
                    .setPriceData(priceData)
                    .setQuantity((long) item.quantity)
                    .build());
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/success")
                .setCancelUrl(frontendUrl + "/checkout")
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);
        return Map.of("url", session.getUrl());
    }
}
