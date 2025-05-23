package com.demo.loan.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "emi_id", nullable = false)
    private Emi emi;

    private BigDecimal transactionAmount;
    private String paymentMethod;
    private LocalDateTime transactionDate;
    private String transactionStatus;

}
