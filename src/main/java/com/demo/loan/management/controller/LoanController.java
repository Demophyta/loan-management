package com.demo.loan.management.controller;

import com.demo.loan.management.model.Loan;
import com.demo.loan.management.service.LoanService;
import com.demo.loan.management.dto.LoanRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan Controller", description = "Handles loan operations such as applying, viewing, and approving loans.")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply for a Loan", description = "Allows a user to apply for a loan using LoanRequestDTO.")
    public ResponseEntity<Loan> applyLoan(@RequestBody LoanRequestDTO loanRequestDTO) {
        return ResponseEntity.ok(loanService.applyLoan(loanRequestDTO));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Loans (Admin)", description = "Fetch all loans, including both approved and pending. Admin-only access.")
    public ResponseEntity<List<Loan>> getAllLoansIncludingPending() {
        return ResponseEntity.ok(loanService.getAllLoansIncludingPending());
    }

    @GetMapping
    @Operation(summary = "Get Approved Loans", description = "Fetch all approved loans.")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Loans by User", description = "Retrieve all loans for a specific user by ID.")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get Loan by ID", description = "Fetch a single loan record by loan ID.")
    public ResponseEntity<Loan> getLoanById(@PathVariable long loanId) {
        return ResponseEntity.ok(loanService.getLoanById(loanId));
    }

    @PutMapping("/{loanId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve Loan", description = "Approve a loan application. Only accessible by ADMIN.")
    public ResponseEntity<Loan> approveLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanService.approveLoan(loanId));
    }
}
