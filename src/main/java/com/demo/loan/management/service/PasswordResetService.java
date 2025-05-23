package com.demo.loan.management.service;

import com.demo.loan.management.exception.ResourceNotFoundException;
import com.demo.loan.management.model.PasswordResetToken;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.PasswordResetTokenRepository;
import com.demo.loan.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final BCryptPasswordEncoder passwordEncoder;

    // EMAIL RESET FLOW
    public void initiateResetByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with provided email."));

        // Delete existing token for the user if any
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(user, token, LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        String resetLink = "https://yourapp.com/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "Click the link to reset your password: " + resetLink);

        log.info("Password reset email sent to {}", email);
    }

    // OTP RESET FLOW
    public void initiateResetByOTP(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with provided phone number."));

        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        // Generate OTP and dummy token
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        String dummyToken = "otp-" + UUID.randomUUID();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setOtp(otp);
        resetToken.setToken(dummyToken);
        resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        tokenRepository.save(resetToken);

        smsService.sendSms(user.getPhoneNumber(), "Your OTP for password reset is: " + otp);
        log.info("Password reset OTP sent to {}", phoneNumber);
        log.info("Generated OTP for {}: {}", phoneNumber, otp);
    }


    // RESET VIA EMAIL LINK
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token."));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        log.info("Password reset via email successful for user ID {}", user.getUserId());
    }

    // RESET VIA OTP
    public void resetPasswordWithOTP(String otp, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByOtp(otp)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired OTP."));

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);

        log.info("Password reset via OTP successful for user ID {}", user.getUserId());
    }
}
