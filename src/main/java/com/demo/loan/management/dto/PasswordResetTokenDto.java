package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO for resetting password using email token")
public class PasswordResetTokenDto {

    @Schema(description = "Reset token received via email", example = "reset-token-123")
    private String token;

    @Schema(description = "New password to set", example = "myNewPassword")
    private String newPassword;
}
