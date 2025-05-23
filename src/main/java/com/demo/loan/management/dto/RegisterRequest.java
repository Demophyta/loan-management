package com.demo.loan.management.dto;

import com.demo.loan.management.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for user registration")
public class RegisterRequest {

    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @Schema(description = "User's password", example = "password123")
    private String password;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's phone number", example = "+123456789")
    private String phoneNumber;

    @Schema(description = "User's address", example = "123 Main Street")
    private String address;

    @Schema(description = "User's role (ADMIN/USER)", example = "USER")
    private Role role;
}
