package com.demo.loan.management.controller;

import com.demo.loan.management.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendEmailSuccess() {
        // Arrange
        String to = "test@example.com";
        String subject = "Hello";
        String body = "Test Body";

        doNothing().when(emailService).sendEmail(to, subject, body);

        // Act
        String response = emailController.sendEmail(to, subject, body);

        // Assert
        assertEquals("Email sent successfully!", response);
        verify(emailService, times(1)).sendEmail(to, subject, body);
    }

    @Test
    void testSendEmailFailure() {
        // Arrange
        String to = "test@example.com";
        String subject = "Hello";
        String body = "Test Body";

        doThrow(new RuntimeException("Failed")).when(emailService).sendEmail(to, subject, body);

        // Act
        String response = emailController.sendEmail(to, subject, body);

        // Assert
        assertEquals("An error occurred while sending email.", response);
        verify(emailService, times(1)).sendEmail(to, subject, body);
    }
}
