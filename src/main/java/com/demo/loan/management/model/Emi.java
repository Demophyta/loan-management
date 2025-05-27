package com.demo.loan.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "emis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emiId;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    private BigDecimal emiAmount;
    private String status;
    private LocalDateTime dueDate;
    private String paymentStatus;

    @Column(name = "paid_on")
    private LocalDateTime paidOn;
    private String paymentMonth;
    private int paymentYear;

}
