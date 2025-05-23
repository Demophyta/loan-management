package com.demo.loan.management.service;

import com.demo.loan.management.dto.LoanRequestDTO;
import com.demo.loan.management.exception.BadRequestException;
import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.LoanRepository;
import com.demo.loan.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmiRepository emiRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setEmail("user@example.com");
        user.setRole(Role.USER);  // can change to Role.ADMIN for other tests

        // Mock Security Context
        when(userDetails.getUsername()).thenReturn(user.getEmail());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void applyLoan_ShouldSaveLoan_WhenValidRequest() {
        LoanRequestDTO request = new LoanRequestDTO();
        request.setUserId(user.getUserId());
        request.setLoanAmount(new BigDecimal("10000"));
        request.setInterestRate(new BigDecimal("5"));
        request.setLoanTenure(12);
        request.setLoanType("PERSONAL");

        when(loanRepository.existsActiveLoan(user.getUserId(), "PERSONAL")).thenReturn(false);

        Loan savedLoan = new Loan();
        savedLoan.setLoanId(1L);
        savedLoan.setLoanAmount(request.getLoanAmount());

        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

        Loan result = loanService.applyLoan(request);

        assertNotNull(result);
        assertEquals(savedLoan.getLoanAmount(), result.getLoanAmount());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void applyLoan_ShouldThrowException_WhenUserIdMismatch() {
        LoanRequestDTO request = new LoanRequestDTO();
        request.setUserId(999L);  // different from authenticated user

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                loanService.applyLoan(request));

        assertEquals("Unauthorized: You can only apply for your own loan.", exception.getMessage());
    }

    @Test
    void approveLoan_ShouldApproveLoanSuccessfully() {
        user.setRole(Role.ADMIN); // only admins can approve

        Loan loan = new Loan();
        loan.setLoanId(1L);
        loan.setLoanAmount(new BigDecimal("10000"));
        loan.setInterestRate(new BigDecimal("5"));
        loan.setLoanTenure(12);
        loan.setLoanStatus("PENDING");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.approveLoan(1L);

        assertEquals("APPROVED", result.getLoanStatus());
        assertTrue(result.getEmiAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getTotalRepayable().compareTo(BigDecimal.ZERO) > 0);
        verify(emiRepository, times(12)).save(any()); // 12 EMIs created
    }

    @Test
    void approveLoan_ShouldThrow_WhenLoanAlreadyApproved() {
        user.setRole(Role.ADMIN);

        Loan loan = new Loan();
        loan.setLoanId(1L);
        loan.setLoanStatus("APPROVED");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                loanService.approveLoan(1L));

        assertEquals("Loan is already approved.", exception.getMessage());
    }

    @Test
    void approveLoan_ShouldThrow_WhenUserNotAdmin() {
        user.setRole(Role.USER); // Not admin

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                loanService.approveLoan(1L));

        assertEquals("Unauthorized: Only admins can approve loans.", exception.getMessage());
    }

    @Test
    void getLoanById_ShouldReturnLoan_WhenExists() {
        Loan loan = new Loan();
        loan.setLoanId(1L);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.getLoanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getLoanId());
    }

    @Test
    void getLoanById_ShouldThrow_WhenLoanNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                loanService.getLoanById(1L));

        assertEquals("Loan not found with ID: 1", exception.getMessage());
    }
}
