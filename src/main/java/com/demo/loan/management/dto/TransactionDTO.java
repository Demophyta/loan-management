package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for transaction details")
public class TransactionDTO {

    @Schema(description = "EMI ID associated with this transaction", example = "1")
    private Long emiId;

    @Schema(description = "Amount of the transaction", example = "5000.0")
    private Double transactionAmount;

    @Schema(description = "Method of payment", example = "CARD")
    private String paymentMethod;

    @Schema(description = "Date of transaction", example = "2025-04-06T14:30:00")
    private Date transactionDate;

    @Schema(description = "Transaction status (e.g., SUCCESS, FAILED)", example = "SUCCESS")
    private String transactionStatus;
}
