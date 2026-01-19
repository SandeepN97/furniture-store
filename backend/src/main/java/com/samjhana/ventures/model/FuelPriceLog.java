package com.samjhana.ventures.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_price_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuelPriceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Column(name = "buying_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal buyingPrice;

    @Column(name = "selling_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal sellingPrice;

    @Column(name = "quantity_purchased", precision = 10, scale = 2)
    private BigDecimal quantityPurchased; // in liters

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum FuelType {
        PETROL,
        DIESEL,
        KEROSENE
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
