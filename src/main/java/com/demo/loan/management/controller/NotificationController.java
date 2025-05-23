package com.demo.loan.management.controller;

import com.demo.loan.management.model.Notification;
import com.demo.loan.management.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Controller", description = "Handles notification delivery (Email, SMS) and management.")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create Notification", description = "Create a notification for a specific user.")
    public ResponseEntity<Notification> createNotification(@PathVariable Long userId, @RequestBody Notification notification) {
        return ResponseEntity.ok(notificationService.createNotification(userId, notification));
    }

    @GetMapping
    @Operation(summary = "Get All Notifications", description = "Retrieve all notifications in the system.")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Notifications", description = "Fetch all notifications sent to a specific user.")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get Notification by ID", description = "Retrieve a single notification by its ID.")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId));
    }

    @PutMapping("/{notificationId}")
    @Operation(summary = "Update Notification", description = "Update the contents or status of a notification.")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long notificationId, @RequestBody Notification updatedNotification) {
        return ResponseEntity.ok(notificationService.updateNotification(notificationId, updatedNotification));
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete Notification", description = "Delete a notification from the system.")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted successfully.");
    }

    @PostMapping("/send-loan-approval/{userId}")
    @Operation(summary = "Send Loan Approval Notification", description = "Send loan approval notification (email or SMS).")
    public ResponseEntity<String> sendLoanApprovalNotification(@PathVariable Long userId) {
        notificationService.sendLoanApprovalNotification(userId);
        return ResponseEntity.ok("Loan approval notification sent successfully.");
    }
}
