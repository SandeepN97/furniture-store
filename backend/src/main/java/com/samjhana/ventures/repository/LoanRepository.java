package com.samjhana.ventures.repository;

import com.samjhana.ventures.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(Loan.LoanStatus status);

    List<Loan> findByBusinessId(Long businessId);

    List<Loan> findByBusinessIdAndStatus(Long businessId, Loan.LoanStatus status);
}
