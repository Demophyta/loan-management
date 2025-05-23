package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(description = "DTO for requesting password reset via email")
public class PasswordResetEmailRequestDto {

    @Schema(description = "User email address", example = "user@example.com")
    private String email;
}
