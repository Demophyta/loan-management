package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for resetting password using OTP")
public class PasswordResetOtpResetRequestDto {

    @Schema(description = "One-Time Password sent via SMS", example = "123456")
    private String otp;

    @Schema(description = "New password to be set", example = "newSecurePassword123")
    private String newPassword;
}
