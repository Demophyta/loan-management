package com.demo.loan.management.service;

import com.demo.loan.management.dto.TransactionDTO;
import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.model.Transaction;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final EmiRepository emiRepository;

    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_COMPLETED = "COMPLETED";

    /**
     *  Process a transaction
     */
    @Transactional
    public Transaction processTransaction(TransactionDTO transactionDTO) {
        Emi emi = emiRepository.findById(transactionDTO.getEmiId())
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found with ID: " + transactionDTO.getEmiId()));

        Transaction transaction = Transaction.builder()
                .emi(emi)
                .transactionAmount(BigDecimal.valueOf(transactionDTO.getTransactionAmount()))
                .paymentMethod(transactionDTO.getPaymentMethod())
                .transactionDate(LocalDateTime.now())
                .transactionStatus(STATUS_COMPLETED)
                .build();

        return transactionRepository.save(transaction);
    }

    /**
      Pay EMI - Validates and processes the payment.
     */
    @Transactional
    public String payEmi(Long emiId, double amount) {
        Emi emi = emiRepository.findById(emiId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found with ID: " + emiId));

        // Check if EMI is already paid
        if (STATUS_PAID.equalsIgnoreCase(emi.getStatus())) {
            throw new IllegalStateException("This EMI has already been paid.");
        }

        //  Convert amount to BigDecimal before comparison
        BigDecimal paymentAmount = BigDecimal.valueOf(amount);

        // Ensure the payment amount matches EMI amount
        if (paymentAmount.compareTo(emi.getEmiAmount()) != 0) {
            throw new IllegalArgumentException("Invalid amount. EMI amount must be: " + emi.getEmiAmount());
        }

        // Check for duplicate payment
        boolean exists = transactionRepository.existsByEmiEmiIdAndTransactionStatus(emiId, STATUS_SUCCESS);
        if (exists) {
            throw new IllegalStateException("Duplicate payment detected for EMI ID: " + emiId);
        }

        //  Update EMI as paid
        emi.setStatus(STATUS_PAID);
        emi.setPaidOn(LocalDateTime.now());
        emi.setPaymentStatus(STATUS_SUCCESS);
        emiRepository.save(emi);

        // ✅ Create a transaction record
        Transaction transaction = Transaction.builder()
                .emi(emi)
                .transactionAmount(paymentAmount)
                .paymentMethod("ONLINE")
                .transactionStatus(STATUS_SUCCESS)
                .transactionDate(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        return "EMI payment successful!";
    }

    /**
      Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     Get transactions by Loan ID
     */
    public List<Transaction> getTransactionsByLoanId(Long loanId) {
        return transactionRepository.findByEmi_Loan_LoanId(loanId);
    }

    /**
     Get transaction by ID
     */
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));
    }

    /**
      Get transactions by EMI ID
     */
    public List<Transaction> getTransactionsByEmiId(Long emiId) {
        return transactionRepository.findByEmiEmiId(emiId);
    }
}
