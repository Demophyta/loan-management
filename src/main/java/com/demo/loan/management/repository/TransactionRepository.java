package com.demo.loan.management.repository;

import com.demo.loan.management.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByEmiEmiId(Long emiId);
    boolean existsByEmiEmiIdAndTransactionStatus(Long emiId, String transactionStatus);
    List<Transaction> findByEmi_Loan_LoanId(Long loanId);


}
