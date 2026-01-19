package com.samjhana.ventures.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ev_meter_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EVMeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reading_date", nullable = false, unique = true)
    private LocalDate readingDate;

    @Column(name = "opening_reading", precision = 10, scale = 2, nullable = false)
    private BigDecimal openingReading;

    @Column(name = "closing_reading", precision = 10, scale = 2, nullable = false)
    private BigDecimal closingReading;

    @Column(name = "units_consumed", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitsConsumed; // Calculated: closing - opening

    @Column(name = "rate_per_unit", precision = 10, scale = 2)
    private BigDecimal ratePerUnit;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void calculateUnitsConsumed() {
        if (openingReading != null && closingReading != null) {
            this.unitsConsumed = closingReading.subtract(openingReading);
        }
        if (unitsConsumed != null && ratePerUnit != null) {
            this.totalRevenue = unitsConsumed.multiply(ratePerUnit);
        }
    }
}
