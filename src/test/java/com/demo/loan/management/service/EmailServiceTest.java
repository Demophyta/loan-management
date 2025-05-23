package com.demo.loan.management.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_ShouldSendEmailSuccessfully() {

        String to = "recipient@example.com";
        String subject = "Test Subject";
        String text = "This is a test email.";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendLoanApprovalEmail_ShouldSendLoanApprovalEmail() {
        // Arrange
        String email = "recipient@example.com";
        String loanDetails = "Loan Amount: 10000.0, Interest Rate: 5%, Tenure: 12 months.";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));


        emailService.sendLoanApprovalEmail(email, loanDetails);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmiReminderEmail_ShouldSendEmiReminderEmail() {

        String email = "recipient@example.com";
        String emiDetails = "EMI Amount: 5000.0, Due Date: 2025-06-01.";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmiReminderEmail(email, emiDetails);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
