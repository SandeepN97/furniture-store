package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBusinessIdOrderByTransactionDateDesc(Long businessId);

    List<Transaction> findByBusinessIdAndTransactionDateBetween(
        Long businessId, LocalDateTime startDate, LocalDateTime endDate
    );

    List<Transaction> findByTypeOrderByTransactionDateDesc(Transaction.TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.business.id = :businessId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.type = :type")
    List<Transaction> findByBusinessAndDateRangeAndType(
        @Param("businessId") Long businessId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("type") Transaction.TransactionType type
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.business.id = :businessId " +
           "AND t.type IN :types AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double sumAmountByBusinessAndTypesAndDateRange(
        @Param("businessId") Long businessId,
        @Param("types") List<Transaction.TransactionType> types,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
