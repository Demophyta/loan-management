package com.demo.loan.management.controller;

import com.demo.loan.management.dto.LoanRequestDTO;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanControllerTest {

    private LoanService loanService;
    private LoanController loanController;

    @BeforeEach
    void setUp() {
        loanService = mock(LoanService.class);
        loanController = new LoanController(loanService);
    }

    @Test
    void testApplyLoan() {
        LoanRequestDTO request = new LoanRequestDTO();
        // Populate request DTO if needed

        Loan mockLoan = new Loan();
        mockLoan.setLoanAmount(BigDecimal.valueOf(10000));
        when(loanService.applyLoan(request)).thenReturn(mockLoan);

        ResponseEntity<Loan> response = loanController.applyLoan(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLoan, response.getBody());
        verify(loanService).applyLoan(request);
    }

    @Test
    void testGetAllLoansIncludingPending() {
        List<Loan> mockLoans = Arrays.asList(new Loan(), new Loan());
        when(loanService.getAllLoansIncludingPending()).thenReturn(mockLoans);

        ResponseEntity<List<Loan>> response = loanController.getAllLoansIncludingPending();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        verify(loanService).getAllLoansIncludingPending();
    }

    @Test
    void testGetAllLoans() {
        List<Loan> mockLoans = List.of(new Loan());
        when(loanService.getAllLoans()).thenReturn(mockLoans);

        ResponseEntity<List<Loan>> response = loanController.getAllLoans();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        verify(loanService).getAllLoans();
    }

    @Test
    void testGetLoansByUser() {
        Long userId = 1L;
        List<Loan> mockLoans = List.of(new Loan(), new Loan());
        when(loanService.getLoansByUserId(userId)).thenReturn(mockLoans);

        ResponseEntity<List<Loan>> response = loanController.getLoansByUser(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        verify(loanService).getLoansByUserId(userId);
    }

    @Test
    void testGetLoanById() {
        long loanId = 10L;
        Loan mockLoan = new Loan();
        when(loanService.getLoanById(loanId)).thenReturn(mockLoan);

        ResponseEntity<Loan> response = loanController.getLoanById(loanId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLoan, response.getBody());
        verify(loanService).getLoanById(loanId);
    }

    @Test
    void testApproveLoan() {
        long loanId = 123L;
        Loan mockLoan = new Loan();
        when(loanService.approveLoan(loanId)).thenReturn(mockLoan);

        ResponseEntity<Loan> response = loanController.approveLoan(loanId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockLoan, response.getBody());
        verify(loanService).approveLoan(loanId);
    }
}
