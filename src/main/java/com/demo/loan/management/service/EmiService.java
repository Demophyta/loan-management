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
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmiService {

    private final EmiRepository emiRepository;
    private final LoanRepository loanRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // Create EMI for a loan
    public Emi createEmi(EmiPaymentRequestDTO emiDTO) {
        Loan loan = loanRepository.findById(emiDTO.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + emiDTO.getLoanId()));

        Emi emi = Emi.builder()
                .loan(loan)
                .emiAmount(emiDTO.getEmiAmount())
                .dueDate(emiDTO.getDueDate())
                .status("PENDING")
                .paymentStatus("DUE")
                .build();

        return emiRepository.save(emi);
    }

    // Pay an EMI
    @Transactional
    public String payEmi(Long emiId, EmiPaymentRequestDTO paymentRequest) {
        String email = getAuthenticatedUserEmail();
        User authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Emi emi = emiRepository.findById(emiId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI not found with ID: " + emiId));

        Loan loan = emi.getLoan();
        User loanOwner = loan.getUser();

        if (!loanOwner.getUserId().equals(authenticatedUser.getUserId())) {
            throw new BadRequestException("Unauthorized: You can only pay EMIs for your own loan.");
        }

        if ("PAID".equalsIgnoreCase(emi.getStatus())) {
            throw new BadRequestException("This EMI has already been paid.");
        }

        if (paymentRequest.getEmiAmount() == null || paymentRequest.getEmiAmount().compareTo(emi.getEmiAmount()) != 0) {
            throw new BadRequestException("Incorrect payment amount. Please pay the exact EMI amount.");
        }

        if (paymentRequest.getMonth() != null && paymentRequest.getYear() != null) {
            emi.setPaymentMonth(getMonthName(paymentRequest.getMonth()));
            emi.setPaymentYear(paymentRequest.getYear());
        } else {
            LocalDateTime now = LocalDateTime.now();
            emi.setPaymentMonth(getMonthName(now.getMonthValue()));
            emi.setPaymentYear(now.getYear());
        }

        emi.setStatus("PAID");
        emi.setPaymentStatus("COMPLETED");
        emi.setPaidOn(LocalDateTime.now());
        emiRepository.save(emi);

        Transaction transaction = Transaction.builder()
                .emi(emi)
                .transactionAmount(paymentRequest.getEmiAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .transactionDate(LocalDateTime.now())
                .transactionStatus("SUCCESS")
                .build();

        transactionRepository.save(transaction);

        long pendingEmis = emiRepository.countByLoanLoanIdAndStatus(loan.getLoanId(), "PENDING");
        if (pendingEmis == 0) {
            loan.setLoanStatus("COMPLETED");
            loanRepository.save(loan);
        }

        return "EMI Payment Successful";
    }

    //  New: Get EMIs by loan ID
    public List<Emi> getEmisByLoanId(Long loanId) {
        return emiRepository.findByLoanLoanId(loanId);
    }

    // New: Get EMI payment history for authenticated user
    public List<Emi> getEmiHistoryForUser() {
        String email = getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return emiRepository.findByLoanUserUserId(user.getUserId());
    }

    private String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        throw new BadRequestException("Unauthorized: Unable to fetch user details.");
    }

    private String getMonthName(int monthNumber) {
        return Month.of(monthNumber).getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH);
    }
}
