package com.demo.loan.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Used for email-based password reset
    @Column(nullable = true, unique = true)
    private String token;

    // Used for OTP-based password reset
    @Column(length = 6)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    /**
     * Constructor for email token reset
     */
    public PasswordResetToken(User user, String token, LocalDateTime expiryTime) {
        this.user = user;
        this.token = token;
        this.expiryTime = expiryTime;
    }

    /**
     * Constructor for OTP reset
     */
    public PasswordResetToken(User user, String otp, LocalDateTime expiryTime, boolean isOtp) {
        this.user = user;
        this.otp = otp;
        this.expiryTime = expiryTime;
    }
}
