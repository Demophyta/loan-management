package com.demo.loan.management.service;

import com.demo.loan.management.dto.TransactionDTO;
import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.model.Transaction;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EmiRepository emiRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessTransaction_ShouldSaveTransaction() {
        Long emiId = 1L;
        TransactionDTO dto = new TransactionDTO();
        dto.setEmiId(emiId);
        dto.setTransactionAmount(1000.0);
        dto.setPaymentMethod("CARD");

        Emi emi = new Emi();
        emi.setEmiId(emiId);

        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved transaction

        Transaction result = transactionService.processTransaction(dto);

        assertEquals(emi, result.getEmi());
        assertEquals(BigDecimal.valueOf(1000.0), result.getTransactionAmount());
        assertEquals("CARD", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getTransactionStatus());
    }

    @Test
    void testPayEmi_SuccessfulPayment() {
        Long emiId = 2L;
        BigDecimal emiAmount = BigDecimal.valueOf(1500.0);

        Emi emi = new Emi();
        emi.setEmiId(emiId);
        emi.setEmiAmount(emiAmount);
        emi.setStatus("PENDING");

        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));
        when(transactionRepository.existsByEmiEmiIdAndTransactionStatus(eq(emiId), eq("SUCCESS"))).thenReturn(false);
        when(emiRepository.save(any(Emi.class))).thenReturn(emi);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String result = transactionService.payEmi(emiId, 1500.0);

        assertEquals("EMI payment successful!", result);
        assertEquals("PAID", emi.getStatus());
        assertEquals("SUCCESS", emi.getPaymentStatus());
        assertNotNull(emi.getPaidOn());
    }

    @Test
    void testPayEmi_ThrowsIfEmiAlreadyPaid() {
        Long emiId = 3L;
        Emi emi = new Emi();
        emi.setEmiId(emiId);
        emi.setStatus("PAID");

        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            transactionService.payEmi(emiId, 1500.0);
        });
        assertEquals("This EMI has already been paid.", ex.getMessage());
    }

    @Test
    void testPayEmi_ThrowsIfInvalidAmount() {
        Long emiId = 4L;
        Emi emi = new Emi();
        emi.setEmiId(emiId);
        emi.setEmiAmount(BigDecimal.valueOf(1200.0));
        emi.setStatus("PENDING");

        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.payEmi(emiId, 1000.0);
        });

        assertEquals("Invalid amount. EMI amount must be: 1200.0", ex.getMessage());
    }

    @Test
    void testPayEmi_ThrowsIfDuplicatePayment() {
        Long emiId = 5L;
        Emi emi = new Emi();
        emi.setEmiId(emiId);
        emi.setEmiAmount(BigDecimal.valueOf(2000.0));
        emi.setStatus("PENDING");

        when(emiRepository.findById(emiId)).thenReturn(Optional.of(emi));
        when(transactionRepository.existsByEmiEmiIdAndTransactionStatus(eq(emiId), eq("SUCCESS"))).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            transactionService.payEmi(emiId, 2000.0);
        });

        assertEquals("Duplicate payment detected for EMI ID: 5", ex.getMessage());
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.getTransactionById(99L);
        });
    }

    @Test
    void testGetTransactionsByLoanId_CallsRepository() {
        Long loanId = 10L;
        transactionService.getTransactionsByLoanId(loanId);
        verify(transactionRepository, times(1)).findByEmi_Loan_LoanId(loanId);
    }

    @Test
    void testGetTransactionsByEmiId_CallsRepository() {
        Long emiId = 11L;
        transactionService.getTransactionsByEmiId(emiId);
        verify(transactionRepository, times(1)).findByEmiEmiId(emiId);
    }

    @Test
    void testGetAllTransactions_CallsRepository() {
        transactionService.getAllTransactions();
        verify(transactionRepository, times(1)).findAll();
    }
}
