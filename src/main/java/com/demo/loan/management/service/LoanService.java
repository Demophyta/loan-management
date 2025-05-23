package com.demo.loan.management.service;

import com.demo.loan.management.dto.LoanRequestDTO;
import com.demo.loan.management.exception.BadRequestException;
import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.model.Role; // Import Role Enum
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.LoanRepository;
import com.demo.loan.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final EmiRepository emiRepository;

    public List<Loan> getAllLoans() {
        return loanRepository.findByLoanStatus("APPROVED");
    }

    public List<Loan> getAllLoansIncludingPending() {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() != Role.ADMIN) { // Use top-level Role enum here
            throw new BadRequestException("Unauthorized: Only admins can view all loans.");
        }
        return loanRepository.findAll();
    }

    @Transactional
    public Loan applyLoan(LoanRequestDTO loanRequest) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() != Role.USER) {
            throw new BadRequestException("Unauthorized: Only users can apply for a loan.");
        }

        if (!loanRequest.getUserId().equals(authenticatedUser.getUserId())) {
            throw new BadRequestException("Unauthorized: You can only apply for your own loan.");
        }

        if (loanRepository.existsActiveLoan(authenticatedUser.getUserId(), loanRequest.getLoanType())) {
            throw new BadRequestException("You already have an active loan of this type.");
        }

        if (loanRequest.getLoanAmount().compareTo(BigDecimal.ZERO) <= 0 || loanRequest.getInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Invalid loan amount or interest rate.");
        }

        Loan loan = Loan.builder()
                .user(authenticatedUser)
                .loanAmount(loanRequest.getLoanAmount())
                .interestRate(loanRequest.getInterestRate())
                .loanTenure(loanRequest.getLoanTenure())
                .loanType(loanRequest.getLoanType())
                .loanStatus("PENDING")
                .emiAmount(BigDecimal.ZERO)
                .totalRepayable(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan approveLoan(Long loanId) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new BadRequestException("Unauthorized: Only admins can approve loans.");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));

        if ("APPROVED".equalsIgnoreCase(loan.getLoanStatus())) {
            throw new IllegalStateException("Loan is already approved.");
        }

        loan.setLoanStatus("APPROVED");

        BigDecimal totalRepayable = calculateTotalRepayable(loan.getLoanAmount(), loan.getInterestRate(), loan.getLoanTenure());
        BigDecimal emiAmount = calculateEmi(loan.getLoanAmount(), loan.getInterestRate(), loan.getLoanTenure());

        if (emiAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("EMI calculation failed. Please check loan details.");
        }

        loan.setTotalRepayable(totalRepayable);
        loan.setEmiAmount(emiAmount);
        generateEmisForLoan(loan, emiAmount);

        return loanRepository.save(loan);
    }

    private void generateEmisForLoan(Loan loan, BigDecimal emiAmount) {
        for (int i = 1; i <= loan.getLoanTenure(); i++) {
            Emi emi = new Emi();
            emi.setLoan(loan);
            emi.setEmiAmount(emiAmount);
            emi.setDueDate(LocalDateTime.now().plusMonths(i));
            emi.setStatus("PENDING");
            emi.setPaymentStatus("DUE");
            emiRepository.save(emi);
        }
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserUserId(userId);
    }

    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
    }

    private BigDecimal calculateTotalRepayable(BigDecimal loanAmount, BigDecimal interestRate, int tenureInMonths) {
        BigDecimal tenureInYears = BigDecimal.valueOf(tenureInMonths).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal interest = loanAmount.multiply(interestRate).multiply(tenureInYears).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return loanAmount.add(interest);
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int tenureMonths) {
        if (tenureMonths <= 0) {
            throw new BadRequestException("Loan tenure must be greater than 0.");
        }

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }

        BigDecimal numerator = monthlyRate.multiply(principal).multiply(
                BigDecimal.ONE.add(monthlyRate).pow(tenureMonths)
        );
        BigDecimal denominator = BigDecimal.ONE.add(monthlyRate).pow(tenureMonths).subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new BadRequestException("Invalid authentication. Please log in again.");
        }

        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
