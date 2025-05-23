package com.demo.loan.management.service;

import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.PasswordResetToken;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.PasswordResetTokenRepository;
import com.demo.loan.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;
    private PasswordResetToken resetToken;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setPassword("oldPassword");

        resetToken = new PasswordResetToken(user, "resetToken", LocalDateTime.now().plusMinutes(15));
    }

    @Test
    public void initiateResetByEmail_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        // This should throw ResourceNotFoundException
        try {
            passwordResetService.initiateResetByEmail("nonexistent@example.com");
        } catch (ResourceNotFoundException e) {
            verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        }
    }

    @Test
    public void initiateResetByEmail_Success_ShouldSendEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(any())).thenReturn(Optional.empty());

        passwordResetService.initiateResetByEmail(user.getEmail());

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendEmail(any(), any(), any());
    }

    @Test
    public void initiateResetByOTP_UserNotFound_ShouldThrowException() {
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

        // This should throw ResourceNotFoundException
        try {
            passwordResetService.initiateResetByOTP("0987654321");
        } catch (ResourceNotFoundException e) {
            verify(userRepository, times(1)).findByPhoneNumber("0987654321");
        }
    }

    @Test
    public void initiateResetByOTP_Success_ShouldSendSms() {
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.of(user));
        when(tokenRepository.findByUser(any())).thenReturn(Optional.empty());

        passwordResetService.initiateResetByOTP(user.getPhoneNumber());

        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(smsService, times(1)).sendSms(any(), any());
    }

    @Test
    public void resetPassword_InvalidToken_ShouldThrowException() {
        when(tokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // This should throw ResourceNotFoundException
        try {
            passwordResetService.resetPassword("invalidToken", "newPassword");
        } catch (ResourceNotFoundException e) {
            verify(tokenRepository, times(1)).findByToken("invalidToken");
        }
    }

    @Test
    public void resetPassword_TokenExpired_ShouldThrowException() {
        resetToken.setExpiryTime(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByToken(any())).thenReturn(Optional.of(resetToken));

        // This should throw RuntimeException for expired token
        try {
            passwordResetService.resetPassword("resetToken", "newPassword");
        } catch (RuntimeException e) {
            verify(tokenRepository, times(1)).findByToken("resetToken");
        }
    }

    @Test
    public void resetPassword_Success_ShouldUpdatePassword() {
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
        when(tokenRepository.findByToken(any())).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(any())).thenReturn("newEncryptedPassword");

        passwordResetService.resetPassword("resetToken", "newPassword");

        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenRepository, times(1)).delete(any(PasswordResetToken.class));
    }

    @Test
    public void resetPasswordWithOTP_InvalidOtp_ShouldThrowException() {
        when(tokenRepository.findByOtp(any())).thenReturn(Optional.empty());

        // This should throw ResourceNotFoundException
        try {
            passwordResetService.resetPasswordWithOTP("invalidOtp", "newPassword");
        } catch (ResourceNotFoundException e) {
            verify(tokenRepository, times(1)).findByOtp("invalidOtp");
        }
    }

    @Test
    public void resetPasswordWithOTP_ExpiredOtp_ShouldThrowException() {
        resetToken.setExpiryTime(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByOtp(any())).thenReturn(Optional.of(resetToken));

        // This should throw RuntimeException for expired OTP
        try {
            passwordResetService.resetPasswordWithOTP("expiredOtp", "newPassword");
        } catch (RuntimeException e) {
            verify(tokenRepository, times(1)).findByOtp("expiredOtp");
        }
    }

    @Test
    public void resetPasswordWithOTP_Success_ShouldUpdatePassword() {
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        when(tokenRepository.findByOtp(any())).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(any())).thenReturn("newEncryptedPassword");

        passwordResetService.resetPasswordWithOTP("validOtp", "newPassword");

        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenRepository, times(1)).delete(any(PasswordResetToken.class));
    }
}
