package com.samjhana.ventures.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private BusinessUnit business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private String category;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal units; // For quantities (liters, pieces, etc.)

    @Column(name = "meter_reading", precision = 10, scale = 2)
    private BigDecimal meterReading; // For EV charging station

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        INCOME,
        EXPENSE,
        OWNER_DRAW,
        SALARY,
        RENT_COLLECTED,
        FUEL_SALE,
        EV_CHARGING,
        PRODUCT_SALE
    }
}
