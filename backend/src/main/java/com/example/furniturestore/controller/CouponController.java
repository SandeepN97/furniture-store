package com.example.furniturestore.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.furniturestore.model.Coupon;
import com.example.furniturestore.repository.CouponRepository;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponRepository couponRepository;

    public CouponController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public static class CouponRequest {
        public String code;
        public String description;
        public String discountType;
        public BigDecimal discountValue;
        public BigDecimal minPurchaseAmount;
        public String validFrom;
        public String validUntil;
        public Boolean isActive;
        public Integer usageLimit;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal orderTotal = new BigDecimal(request.get("orderTotal").toString());

        return couponRepository.findByCodeIgnoreCase(code).map(coupon -> {
            if (!coupon.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Coupon is not valid or has expired"));
            }

            if (coupon.getMinPurchaseAmount() != null &&
                orderTotal.compareTo(coupon.getMinPurchaseAmount()) < 0) {
                return ResponseEntity.badRequest().body(Map.of("error",
                    "Minimum purchase amount of $" + coupon.getMinPurchaseAmount() + " required"));
            }

            BigDecimal discount;
            if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                discount = orderTotal.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"));
            } else {
                discount = coupon.getDiscountValue();
            }

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "discount", discount,
                "discountType", coupon.getDiscountType(),
                "description", coupon.getDescription()
            ));
        }).orElse(ResponseEntity.badRequest().body(Map.of("error", "Invalid coupon code")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest request) {
        if (couponRepository.existsByCodeIgnoreCase(request.code)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Coupon code already exists"));
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.code.toUpperCase());
        coupon.setDescription(request.description);
        coupon.setDiscountType(Coupon.DiscountType.valueOf(request.discountType));
        coupon.setDiscountValue(request.discountValue);
        coupon.setMinPurchaseAmount(request.minPurchaseAmount);
        coupon.setIsActive(request.isActive != null ? request.isActive : true);
        coupon.setUsageLimit(request.usageLimit);

        if (request.validFrom != null) {
            coupon.setValidFrom(java.time.LocalDateTime.parse(request.validFrom));
        }
        if (request.validUntil != null) {
            coupon.setValidUntil(java.time.LocalDateTime.parse(request.validUntil));
        }

        Coupon saved = couponRepository.save(coupon);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest request) {
        return couponRepository.findById(id).map(coupon -> {
            coupon.setCode(request.code.toUpperCase());
            coupon.setDescription(request.description);
            coupon.setDiscountType(Coupon.DiscountType.valueOf(request.discountType));
            coupon.setDiscountValue(request.discountValue);
            coupon.setMinPurchaseAmount(request.minPurchaseAmount);
            coupon.setIsActive(request.isActive != null ? request.isActive : true);
            coupon.setUsageLimit(request.usageLimit);

            if (request.validFrom != null) {
                coupon.setValidFrom(java.time.LocalDateTime.parse(request.validFrom));
            }
            if (request.validUntil != null) {
                coupon.setValidUntil(java.time.LocalDateTime.parse(request.validUntil));
            }

            Coupon updated = couponRepository.save(coupon);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        if (!couponRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        couponRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
