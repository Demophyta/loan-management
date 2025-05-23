package com.demo.loan.management.controller;

import com.demo.loan.management.dto.TransactionDTO;
import com.demo.loan.management.model.Transaction;
import com.demo.loan.management.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private TransactionController transactionController;

    @Test
    void testProcessTransaction() {

        TransactionDTO transactionDTO = new TransactionDTO();
        Transaction expectedTransaction = new Transaction();


        when(transactionService.processTransaction(transactionDTO)).thenReturn(expectedTransaction);

        ResponseEntity<Transaction> response = transactionController.processTransaction(transactionDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTransaction, response.getBody());
        verify(transactionService).processTransaction(transactionDTO);
    }

    @Test
    void testGetTransactionById() {
        // Setup test data
        Long transactionId = 1L;
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setTransactionId(transactionId);

        // Define behavior of the mocked service
        when(transactionService.getTransactionById(transactionId)).thenReturn(expectedTransaction);

        // Call the controller method
        ResponseEntity<Transaction> response = transactionController.getTransactionById(transactionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTransaction, response.getBody());


        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void testGetAllTransactions() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        ResponseEntity<List<Transaction>> response = transactionController.getAllTransactions();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
        verify(transactionService).getAllTransactions();
    }

    @Test
    void testGetTransactionsByLoanId() {
        Long loanId = 1L;
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        when(transactionService.getTransactionsByLoanId(loanId)).thenReturn(transactions);
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByLoan(loanId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
        verify(transactionService).getTransactionsByLoanId(loanId);
    }

    @Test
    void testGetTransactionsByEmiId() {
        Long emiId = 1L;
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        when(transactionService.getTransactionsByEmiId(emiId)).thenReturn(transactions);
        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByEmi(emiId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
        verify(transactionService).getTransactionsByEmiId(emiId);
    }
}
