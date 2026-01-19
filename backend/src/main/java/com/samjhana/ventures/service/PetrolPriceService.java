package com.samjhana.ventures.service;

import com.samjhana.ventures.model.FuelPriceLog;
import com.samjhana.ventures.repository.FuelPriceLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for handling dynamic fuel pricing and margin calculations
 * for the Petrol Pump business
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PetrolPriceService {

    private final FuelPriceLogRepository fuelPriceLogRepository;

    /**
     * Get current selling price for a fuel type
     */
    public BigDecimal getCurrentSellingPrice(FuelPriceLog.FuelType fuelType) {
        return fuelPriceLogRepository.findByFuelTypeAndIsCurrentTrue(fuelType)
            .map(FuelPriceLog::getSellingPrice)
            .orElse(BigDecimal.ZERO);
    }

    /**
     * Calculate weighted average cost of fuel based on purchase history
     */
    public BigDecimal calculateWeightedAverageCost(FuelPriceLog.FuelType fuelType) {
        List<FuelPriceLog> activeBatches = fuelPriceLogRepository
            .findByFuelTypeAndIsCurrentTrue(fuelType);

        if (activeBatches.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;

        for (FuelPriceLog batch : activeBatches) {
            if (batch.getQuantityPurchased() != null && batch.getBuyingPrice() != null) {
                BigDecimal batchCost = batch.getBuyingPrice()
                    .multiply(batch.getQuantityPurchased());
                totalCost = totalCost.add(batchCost);
                totalQuantity = totalQuantity.add(batch.getQuantityPurchased());
            }
        }

        if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalCost.divide(totalQuantity, 2, RoundingMode.HALF_UP);
    }

    /**
     * Update selling price for a fuel type
     */
    @Transactional
    public FuelPriceLog updateSellingPrice(
        FuelPriceLog.FuelType fuelType,
        BigDecimal newSellingPrice
    ) {
        // Mark current price as not current
        fuelPriceLogRepository.findByFuelTypeAndIsCurrentTrue(fuelType)
            .ifPresent(currentPrice -> {
                currentPrice.setIsCurrent(false);
                fuelPriceLogRepository.save(currentPrice);
            });

        // Create new price log
        FuelPriceLog newPriceLog = new FuelPriceLog();
        newPriceLog.setFuelType(fuelType);
        newPriceLog.setSellingPrice(newSellingPrice);
        newPriceLog.setBuyingPrice(calculateWeightedAverageCost(fuelType));
        newPriceLog.setEffectiveDate(LocalDate.now());
        newPriceLog.setIsCurrent(true);

        log.info("Updated selling price for {} to {}", fuelType, newSellingPrice);
        return fuelPriceLogRepository.save(newPriceLog);
    }

    /**
     * Record a new fuel purchase batch
     */
    @Transactional
    public FuelPriceLog recordFuelPurchase(
        FuelPriceLog.FuelType fuelType,
        BigDecimal quantityPurchased,
        BigDecimal buyingPrice,
        BigDecimal sellingPrice
    ) {
        FuelPriceLog purchaseLog = new FuelPriceLog();
        purchaseLog.setFuelType(fuelType);
        purchaseLog.setQuantityPurchased(quantityPurchased);
        purchaseLog.setBuyingPrice(buyingPrice);
        purchaseLog.setSellingPrice(sellingPrice != null ? sellingPrice : getCurrentSellingPrice(fuelType));
        purchaseLog.setEffectiveDate(LocalDate.now());
        purchaseLog.setIsCurrent(true);

        log.info("Recorded fuel purchase: {} liters of {} at {} per liter",
            quantityPurchased, fuelType, buyingPrice);
        return fuelPriceLogRepository.save(purchaseLog);
    }

    /**
     * Calculate profit for a fuel sale
     */
    public BigDecimal calculateSaleProfit(
        FuelPriceLog.FuelType fuelType,
        BigDecimal litersSold
    ) {
        BigDecimal sellingPrice = getCurrentSellingPrice(fuelType);
        BigDecimal averageCost = calculateWeightedAverageCost(fuelType);

        BigDecimal revenue = sellingPrice.multiply(litersSold);
        BigDecimal cost = averageCost.multiply(litersSold);

        return revenue.subtract(cost);
    }

    /**
     * Calculate profit margin percentage
     */
    public BigDecimal calculateProfitMargin(FuelPriceLog.FuelType fuelType) {
        BigDecimal sellingPrice = getCurrentSellingPrice(fuelType);
        BigDecimal averageCost = calculateWeightedAverageCost(fuelType);

        if (sellingPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal profit = sellingPrice.subtract(averageCost);
        return profit.divide(sellingPrice, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));
    }

    /**
     * Get price history for a fuel type
     */
    public List<FuelPriceLog> getPriceHistory(FuelPriceLog.FuelType fuelType) {
        return fuelPriceLogRepository.findByFuelTypeOrderByEffectiveDateDesc(fuelType);
    }

    /**
     * Get all current prices
     */
    public List<FuelPriceLog> getCurrentPrices() {
        return fuelPriceLogRepository.findByIsCurrentTrue();
    }
}
