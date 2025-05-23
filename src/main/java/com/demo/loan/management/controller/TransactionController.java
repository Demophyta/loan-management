package com.demo.loan.management.controller;

import com.demo.loan.management.model.Transaction;
import com.demo.loan.management.dto.TransactionDTO;
import com.demo.loan.management.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Controller", description = "Handles transaction processing and retrieval.")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/process")
    @Operation(summary = "Process Transaction", description = "Process a new EMI transaction using transaction details.")
    public ResponseEntity<Transaction> processTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.processTransaction(transactionDTO));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get Transaction by ID", description = "Retrieve a transaction by its unique ID.")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionById(transactionId));
    }

    @GetMapping
    @Operation(summary = "Get All Transactions", description = "Retrieve all transaction records.")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/loan/{loanId}")
    @Operation(summary = "Get Transactions by Loan", description = "Retrieve all transactions for a specific loan.")
    public ResponseEntity<List<Transaction>> getTransactionsByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(transactionService.getTransactionsByLoanId(loanId));
    }

    @GetMapping("/emi/{emiId}")
    @Operation(summary = "Get Transactions by EMI", description = "Retrieve all transactions for a specific EMI.")
    public ResponseEntity<List<Transaction>> getTransactionsByEmi(@PathVariable Long emiId) {
        return ResponseEntity.ok(transactionService.getTransactionsByEmiId(emiId));
    }
}
