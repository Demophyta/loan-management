package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Builder
@Schema(description = "DTO for user login response")
public class LoginResponseDTO {

    @Schema(description = "JWT token for authenticated user")
    private String token;

    @Schema(description = "Role of the authenticated user", example = "USER")
    private String role;

    public LoginResponseDTO(String token, String role) {
        this.token = token;
        this.role = role;
    }


}
