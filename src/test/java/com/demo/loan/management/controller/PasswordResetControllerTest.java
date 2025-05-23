package com.demo.loan.management.controller;

import com.demo.loan.management.dto.PasswordResetEmailRequestDto;
import com.demo.loan.management.dto.PasswordResetOtpRequestDto;
import com.demo.loan.management.dto.PasswordResetOtpResetRequestDto;
import com.demo.loan.management.dto.PasswordResetTokenDto;
import com.demo.loan.management.service.PasswordResetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PasswordResetControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestPasswordResetByEmail() {
        PasswordResetEmailRequestDto request = new PasswordResetEmailRequestDto();
        request.setEmail("user@example.com");

        ResponseEntity<String> response = passwordResetController.requestPasswordResetByEmail(request);

        assertEquals("Password reset link sent to email.", response.getBody());
        verify(passwordResetService, times(1)).initiateResetByEmail(request.getEmail());
    }

    @Test
    void testRequestPasswordResetByOtp() {
        PasswordResetOtpRequestDto request = new PasswordResetOtpRequestDto();
        request.setPhoneNumber("9876543210");

        ResponseEntity<String> response = passwordResetController.requestPasswordResetByOtp(request);

        assertEquals("OTP sent to mobile number.", response.getBody());
        verify(passwordResetService, times(1)).initiateResetByOTP(request.getPhoneNumber());
    }

    @Test
    void testResetPasswordWithToken() {
        // Fix: Assuming PasswordResetTokenDto has a no-args constructor and setter method.
        PasswordResetTokenDto request = new PasswordResetTokenDto();
        request.setToken("reset-token");
        request.setNewPassword("newPass");

        ResponseEntity<String> response = passwordResetController.resetPassword(request);

        assertEquals("Password successfully reset.", response.getBody());
        verify(passwordResetService, times(1)).resetPassword(request.getToken(), request.getNewPassword());
    }

    @Test
    void testResetPasswordWithOtp() {
        PasswordResetOtpResetRequestDto request = new PasswordResetOtpResetRequestDto();
        request.setOtp("123456");
        request.setNewPassword("securePass");

        ResponseEntity<String> response = passwordResetController.resetPasswordWithOtp(request);

        assertEquals("Password successfully reset.", response.getBody());
        verify(passwordResetService, times(1)).resetPasswordWithOTP(request.getOtp(), request.getNewPassword());
    }

}
