package com.samjhana.ventures.controller;

import com.samjhana.ventures.model.Transaction;
import com.samjhana.ventures.repository.BusinessUnitRepository;
import com.samjhana.ventures.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionRepository transactionRepository;
    private final BusinessUnitRepository businessUnitRepository;

    @GetMapping("/profit-loss")
    public ResponseEntity<Map<String, Object>> getGlobalProfitLoss(
        @RequestParam(required = false) String period
    ) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        // Default to current month
        if (period == null || period.equals("month")) {
            startDate = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        } else if (period.equals("year")) {
            startDate = endDate.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        } else if (period.equals("week")) {
            startDate = endDate.minusWeeks(1);
        } else {
            startDate = endDate.minusDays(30);
        }

        List<Transaction> transactions = transactionRepository.findAll()
            .stream()
            .filter(t -> t.getTransactionDate().isAfter(startDate))
            .collect(Collectors.toList());

        // Group by business
        Map<String, Map<String, Double>> businessData = new HashMap<>();

        businessUnitRepository.findByActiveTrue().forEach(business -> {
            Map<String, Double> data = new HashMap<>();
            data.put("income", 0.0);
            data.put("expense", 0.0);
            data.put("profit", 0.0);
            businessData.put(business.getName(), data);
        });

        transactions.forEach(t -> {
            String businessName = t.getBusiness().getName();
            Map<String, Double> data = businessData.get(businessName);

            if (data != null) {
                double amount = t.getAmount().doubleValue();

                if (isIncomeType(t.getType())) {
                    data.put("income", data.get("income") + amount);
                } else {
                    data.put("expense", data.get("expense") + amount);
                }
            }
        });

        // Calculate profit
        businessData.forEach((business, data) -> {
            double profit = data.get("income") - data.get("expense");
            data.put("profit", profit);
        });

        // Calculate totals
        double totalIncome = businessData.values().stream()
            .mapToDouble(d -> d.get("income")).sum();
        double totalExpense = businessData.values().stream()
            .mapToDouble(d -> d.get("expense")).sum();
        double totalProfit = totalIncome - totalExpense;

        Map<String, Object> result = new HashMap<>();
        result.put("businesses", businessData);
        result.put("totals", Map.of(
            "income", totalIncome,
            "expense", totalExpense,
            "profit", totalProfit
        ));
        result.put("period", period);
        result.put("startDate", startDate);
        result.put("endDate", endDate);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/business/{businessId}/analytics")
    public ResponseEntity<Map<String, Object>> getBusinessAnalytics(
        @PathVariable Long businessId,
        @RequestParam(required = false) String period
    ) {
        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        if (period == null || period.equals("month")) {
            startDate = endDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        } else if (period.equals("year")) {
            startDate = endDate.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        } else {
            startDate = endDate.minusDays(30);
        }

        List<Transaction> transactions = transactionRepository
            .findByBusinessIdAndTransactionDateBetween(businessId, startDate, endDate);

        double income = transactions.stream()
            .filter(t -> isIncomeType(t.getType()))
            .mapToDouble(t -> t.getAmount().doubleValue())
            .sum();

        double expense = transactions.stream()
            .filter(t -> !isIncomeType(t.getType()))
            .mapToDouble(t -> t.getAmount().doubleValue())
            .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("income", income);
        result.put("expense", expense);
        result.put("profit", income - expense);
        result.put("transactionCount", transactions.size());
        result.put("period", period);

        return ResponseEntity.ok(result);
    }

    private boolean isIncomeType(Transaction.TransactionType type) {
        return type == Transaction.TransactionType.INCOME ||
               type == Transaction.TransactionType.FUEL_SALE ||
               type == Transaction.TransactionType.EV_CHARGING ||
               type == Transaction.TransactionType.PRODUCT_SALE ||
               type == Transaction.TransactionType.RENT_COLLECTED;
    }
}
