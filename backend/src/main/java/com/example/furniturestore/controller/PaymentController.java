package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
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

    @Value("${paypal.client-id:}")
    private String paypalClientId;

    @Value("${paypal.secret:}")
    private String paypalSecret;

    private final RestTemplate restTemplate = new RestTemplate();

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

    @PostMapping("/paypal/create-order")
    public Map<String, String> createPaypalOrder(@RequestBody CreateSessionRequest request) {
        List<Map<String, Object>> items = request.items.stream().map(it -> {
            Product p = productRepository.findById(it.productId).orElseThrow();
            return Map.of(
                "name", p.getName(),
                "unit_amount", Map.of("currency_code", "USD", "value", p.getPrice().toString()),
                "quantity", it.quantity
            );
        }).collect(Collectors.toList());

        Map<String, Object> body = Map.of(
            "intent", "CAPTURE",
            "purchase_units", List.of(Map.of("items", items)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(paypalClientId, paypalSecret);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        Map<?, ?> resp = restTemplate.postForObject("https://api-m.sandbox.paypal.com/v2/checkout/orders", entity, Map.class);
        String id = (String) resp.get("id");
        return Map.of("id", id);
    }
}
