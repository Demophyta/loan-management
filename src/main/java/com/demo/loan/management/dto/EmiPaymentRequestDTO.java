package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request DTO for making EMI payments")
public class EmiPaymentRequestDTO {

    @Schema(description = "ID of the loan for which EMI is being paid", example = "1")
    private Long loanId;

    @Schema(description = "Amount paid for the EMI", example = "5000.00")
    private BigDecimal emiAmount;

    @Schema(description = "Due date of the EMI", example = "2025-05-01T00:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Status of the EMI (e.g., PAID, PENDING)", example = "PAID")
    private String status;

    @Schema(description = "Date and time when the EMI was paid", example = "2025-05-02T10:00:00")
    private LocalDateTime paidOn;

    @Schema(description = "Payment status (e.g., SUCCESS, FAILED)", example = "SUCCESS")
    private String paymentStatus;

    @Schema(description = "Method used for payment (e.g., CARD, BANK_TRANSFER)", example = "CARD")
    private String paymentMethod;

    @Schema(description = "Month of the EMI payment (1-12)", example = "5")
    private Integer month;

    @Schema(description = "Year of the EMI payment", example = "2025")
    private Integer year;

    public void setDueDate(LocalDate localDate) {
    }
}
