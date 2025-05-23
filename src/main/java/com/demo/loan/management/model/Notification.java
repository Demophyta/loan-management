package com.demo.loan.management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    private String notificationType;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    @PrePersist  // 🔴 Auto-set createdAt before inserting into DB
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
