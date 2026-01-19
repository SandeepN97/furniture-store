package com.samjhana.ventures.controller;

import com.samjhana.ventures.model.FuelPriceLog;
import com.samjhana.ventures.service.PetrolPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fuel-prices")
@RequiredArgsConstructor
public class FuelPriceController {

    private final PetrolPriceService petrolPriceService;

    @GetMapping("/current")
    public ResponseEntity<List<FuelPriceLog>> getCurrentPrices() {
        return ResponseEntity.ok(petrolPriceService.getCurrentPrices());
    }

    @GetMapping("/current/{fuelType}")
    public ResponseEntity<BigDecimal> getCurrentPrice(@PathVariable FuelPriceLog.FuelType fuelType) {
        return ResponseEntity.ok(petrolPriceService.getCurrentSellingPrice(fuelType));
    }

    @GetMapping("/history/{fuelType}")
    public ResponseEntity<List<FuelPriceLog>> getPriceHistory(@PathVariable FuelPriceLog.FuelType fuelType) {
        return ResponseEntity.ok(petrolPriceService.getPriceHistory(fuelType));
    }

    @GetMapping("/margin/{fuelType}")
    public ResponseEntity<BigDecimal> getProfitMargin(@PathVariable FuelPriceLog.FuelType fuelType) {
        return ResponseEntity.ok(petrolPriceService.calculateProfitMargin(fuelType));
    }

    @PostMapping("/update-price")
    public ResponseEntity<FuelPriceLog> updateSellingPrice(
        @RequestBody Map<String, Object> request
    ) {
        FuelPriceLog.FuelType fuelType = FuelPriceLog.FuelType.valueOf(
            request.get("fuelType").toString()
        );
        BigDecimal newPrice = new BigDecimal(request.get("sellingPrice").toString());

        FuelPriceLog updated = petrolPriceService.updateSellingPrice(fuelType, newPrice);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/record-purchase")
    public ResponseEntity<FuelPriceLog> recordPurchase(@RequestBody Map<String, Object> request) {
        FuelPriceLog.FuelType fuelType = FuelPriceLog.FuelType.valueOf(
            request.get("fuelType").toString()
        );
        BigDecimal quantity = new BigDecimal(request.get("quantity").toString());
        BigDecimal buyingPrice = new BigDecimal(request.get("buyingPrice").toString());
        BigDecimal sellingPrice = request.containsKey("sellingPrice") ?
            new BigDecimal(request.get("sellingPrice").toString()) : null;

        FuelPriceLog log = petrolPriceService.recordFuelPurchase(
            fuelType, quantity, buyingPrice, sellingPrice
        );
        return ResponseEntity.ok(log);
    }

    @PostMapping("/calculate-profit")
    public ResponseEntity<Map<String, BigDecimal>> calculateProfit(
        @RequestBody Map<String, Object> request
    ) {
        FuelPriceLog.FuelType fuelType = FuelPriceLog.FuelType.valueOf(
            request.get("fuelType").toString()
        );
        BigDecimal litersSold = new BigDecimal(request.get("litersSold").toString());

        BigDecimal profit = petrolPriceService.calculateSaleProfit(fuelType, litersSold);
        BigDecimal sellingPrice = petrolPriceService.getCurrentSellingPrice(fuelType);
        BigDecimal revenue = sellingPrice.multiply(litersSold);

        return ResponseEntity.ok(Map.of(
            "profit", profit,
            "revenue", revenue,
            "litersSold", litersSold
        ));
    }
}
