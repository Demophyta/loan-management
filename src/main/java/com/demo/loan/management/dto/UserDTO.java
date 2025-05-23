package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for user information")
public class UserDTO {

    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "User's address", example = "456 Loan Street")
    private String address;

    @Schema(description = "Role of the user", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;  // You can map Role.USER.name() here
}
