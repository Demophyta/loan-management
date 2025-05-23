package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for requesting password reset OTP via phone number")
public class PasswordResetOtpRequestDto {

    @Schema(description = "User phone number", example = "+1234567890")
    private String phoneNumber;
}