package com.example.furniturestore.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShippingController {
    @GetMapping("/api/shipping/rate")
    public BigDecimal getRate(@RequestParam String provider, @RequestParam BigDecimal weight) {
        // Dummy fixed rate per provider
        if ("UPS".equalsIgnoreCase(provider)) {
            return weight.multiply(BigDecimal.valueOf(1.2));
        } else if ("FEDEX".equalsIgnoreCase(provider)) {
            return weight.multiply(BigDecimal.valueOf(1.5));
        }
        return weight.multiply(BigDecimal.ONE);
    }
}
