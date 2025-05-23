package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for loan applications")
public class LoanRequestDTO {

    @Schema(description = "ID of the user applying for the loan", example = "1")
    private Long userId;

    @Schema(description = "Requested loan amount", example = "100000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Interest rate applicable to the loan", example = "7.5")
    private BigDecimal interestRate;

    @Schema(description = "Tenure of the loan in months", example = "24")
    private Integer loanTenure;

    @Schema(description = "Type of the loan (e.g., PERSONAL, HOME, CAR)", example = "PERSONAL")
    private String loanType;
}
