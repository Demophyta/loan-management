package com.demo.loan.management.service;

import com.demo.loan.management.model.Notification;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.NotificationRepository;
import com.demo.loan.management.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // ✅ Create a new notification
    public Notification createNotification(Long userId, Notification notification) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        notification.setUser(user);
        Notification savedNotification = notificationRepository.save(notification);

        // Send Email Notification
        emailService.sendEmail(
                user.getEmail(),
                "New Notification",
                notification.getMessage()
        );

        return savedNotification;
    }

    //  Get all notifications
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    //  Get notifications by user ID
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserUserId(userId);
    }

    //  Get a single notification by ID
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
    }

    //  Update notification
    public Notification updateNotification(Long notificationId, Notification updatedNotification) {
        Notification existingNotification = getNotificationById(notificationId);
        existingNotification.setMessage(updatedNotification.getMessage());
        existingNotification.setStatus(updatedNotification.getStatus());
        existingNotification.setNotificationType(updatedNotification.getNotificationType());
        return notificationRepository.save(existingNotification);
    }

    //  Delete notification
    public void deleteNotification(Long notificationId) {
        Notification notification = getNotificationById(notificationId);
        notificationRepository.delete(notification);
    }

    //  Send loan approval notification
    public Notification sendLoanApprovalNotification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setNotificationType("Loan Approval");
        notification.setMessage("Congratulations! Your loan has been approved.");
        notification.setStatus("UNREAD");

        Notification savedNotification = notificationRepository.save(notification);

        // Send Email Notification
        emailService.sendEmail(
                user.getEmail(),
                "Loan Approved",
                "Dear " + user.getFirstName() + ",\n\nYour loan application has been approved.\n\nThank you!"
        );

        return savedNotification;
    }
}
