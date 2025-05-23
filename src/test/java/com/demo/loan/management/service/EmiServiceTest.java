package com.demo.loan.management.service;

import com.demo.loan.management.dto.EmiPaymentRequestDTO;
import com.demo.loan.management.exception.BadRequestException;
import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.model.Transaction;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.LoanRepository;
import com.demo.loan.management.repository.TransactionRepository;
import com.demo.loan.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmiServiceTest {

    @InjectMocks
    private EmiService emiService;

    @Mock
    private EmiRepository emiRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testEmail);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createEmi_ShouldReturnSavedEmi() {
        Loan loan = Loan.builder().loanId(1L).build();
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setLoanId(1L);
        dto.setEmiAmount(BigDecimal.valueOf(1000));
        dto.setDueDate(LocalDate.now().plusDays(30));

        Emi emi = Emi.builder()
                .emiId(1L)
                .loan(loan)
                .emiAmount(dto.getEmiAmount())
                .dueDate(dto.getDueDate())
                .status("PENDING")
                .paymentStatus("DUE")
                .build();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(emiRepository.save(any(Emi.class))).thenReturn(emi);

        Emi result = emiService.createEmi(dto);

        assertEquals(1L, result.getEmiId());
        assertEquals(BigDecimal.valueOf(1000), result.getEmiAmount());
        assertEquals("PENDING", result.getStatus());
        assertEquals("DUE", result.getPaymentStatus());
    }

    @Test
    void createEmi_ShouldThrow_WhenLoanNotFound() {
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setLoanId(999L);

        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> emiService.createEmi(dto));

        assertEquals("Loan not found with ID: 999", exception.getMessage());
    }

    @Test
    void payEmi_ShouldSucceed_WhenAllDataIsValid() {
        User user = User.builder().userId(1L).email(testEmail).build();
        Loan loan = Loan.builder().loanId(101L).user(user).loanStatus("APPROVED").build();
        Emi emi = Emi.builder().emiId(1001L).loan(loan)
                .emiAmount(BigDecimal.valueOf(1000))
                .status("PENDING")
                .paymentStatus("DUE").build();

        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setEmiAmount(BigDecimal.valueOf(1000));
        dto.setPaymentMethod("CARD");

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(emiRepository.findById(1001L)).thenReturn(Optional.of(emi));
        when(emiRepository.countByLoanLoanIdAndStatus(101L, "PENDING")).thenReturn(0L);

        String result = emiService.payEmi(1001L, dto);

        assertEquals("✅ EMI Payment Successful", result);
        assertEquals("PAID", emi.getStatus());
        assertEquals("COMPLETED", emi.getPaymentStatus());

        verify(emiRepository).save(emi);
        verify(transactionRepository).save(any(Transaction.class));
        verify(loanRepository).save(loan);
    }

    @Test
    void payEmi_ShouldThrow_WhenEmiNotFound() {
        // Arrange: mock user so the method can proceed to EMI check
        User user = User.builder().userId(1L).email(testEmail).build();
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setEmiAmount(BigDecimal.valueOf(1000));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(emiRepository.findById(1001L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> emiService.payEmi(1001L, dto));

        assertEquals("EMI not found with ID: 1001", exception.getMessage());
    }

    @Test
    void payEmi_ShouldThrow_WhenUserNotOwner() {
        User loggedInUser = User.builder().userId(2L).email(testEmail).build();
        User loanOwner = User.builder().userId(1L).email("owner@example.com").build();

        Loan loan = Loan.builder().loanId(101L).user(loanOwner).build();
        Emi emi = Emi.builder().emiId(1001L).loan(loan)
                .emiAmount(BigDecimal.valueOf(1000))
                .status("PENDING")
                .paymentStatus("DUE").build();

        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setEmiAmount(BigDecimal.valueOf(1000));

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(loggedInUser));
        when(emiRepository.findById(1001L)).thenReturn(Optional.of(emi));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> emiService.payEmi(1001L, dto));

        assertEquals("Unauthorized: You can only pay EMIs for your own loan.", exception.getMessage());
    }

    @Test
    void getEmiHistoryForUser_ShouldReturnEmis() {
        User user = User.builder().userId(1L).email(testEmail).build();
        Emi emi1 = Emi.builder().emiId(1L).build();
        Emi emi2 = Emi.builder().emiId(2L).build();

        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
        when(emiRepository.findByLoanUserUserId(1L)).thenReturn(List.of(emi1, emi2));

        List<Emi> result = emiService.getEmiHistoryForUser();

        assertEquals(2, result.size());
    }

    @Test
    void getEmiHistoryForUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> emiService.getEmiHistoryForUser());

        assertEquals("User not found with email: test@example.com", exception.getMessage());
    }

    @Test
    void getEmisByLoanId_ShouldReturnEmis() {
        Loan loan = Loan.builder().loanId(101L).build();
        Emi emi1 = Emi.builder().emiId(1L).loan(loan).build();
        Emi emi2 = Emi.builder().emiId(2L).loan(loan).build();

        when(emiRepository.findByLoanLoanId(101L)).thenReturn(List.of(emi1, emi2));

        List<Emi> result = emiService.getEmisByLoanId(101L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getEmiId());
    }
}
