package com.example.furniturestore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.Coupon;
import com.example.furniturestore.repository.CouponRepository;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponRepository repository;
    public CouponController(CouponRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(repository.save(coupon));
    }
}
