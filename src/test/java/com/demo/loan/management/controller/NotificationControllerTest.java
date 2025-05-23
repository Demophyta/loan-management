package com.demo.loan.management.controller;

import com.demo.loan.management.model.Notification;
import com.demo.loan.management.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    private NotificationService notificationService;
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        notificationController = new NotificationController(notificationService);
    }

    @Test
    void testCreateNotification() {
        Long userId = 1L;
        Notification notification = new Notification();
        when(notificationService.createNotification(userId, notification)).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.createNotification(userId, notification);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(notification, response.getBody());
        verify(notificationService).createNotification(userId, notification);
    }

    @Test
    void testGetAllNotifications() {
        List<Notification> notifications = Collections.singletonList(new Notification());
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(notifications, response.getBody());
        verify(notificationService).getAllNotifications();
    }

    @Test
    void testGetNotificationsByUserId() {
        Long userId = 1L;
        List<Notification> notifications = Collections.singletonList(new Notification());
        when(notificationService.getNotificationsByUserId(userId)).thenReturn(notifications);

        ResponseEntity<List<Notification>> response = notificationController.getNotificationsByUserId(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(notifications, response.getBody());
        verify(notificationService).getNotificationsByUserId(userId);
    }

    @Test
    void testGetNotificationById() {
        Long notificationId = 1L;
        Notification notification = new Notification();
        when(notificationService.getNotificationById(notificationId)).thenReturn(notification);

        ResponseEntity<Notification> response = notificationController.getNotificationById(notificationId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(notification, response.getBody());
        verify(notificationService).getNotificationById(notificationId);
    }

    @Test
    void testUpdateNotification() {
        Long notificationId = 1L;
        Notification updated = new Notification();
        when(notificationService.updateNotification(notificationId, updated)).thenReturn(updated);

        ResponseEntity<Notification> response = notificationController.updateNotification(notificationId, updated);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(updated, response.getBody());
        verify(notificationService).updateNotification(notificationId, updated);
    }

    @Test
    void testDeleteNotification() {
        Long notificationId = 1L;

        ResponseEntity<String> response = notificationController.deleteNotification(notificationId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Notification deleted successfully.", response.getBody());
        verify(notificationService).deleteNotification(notificationId);
    }

    @Test
    void testSendLoanApprovalEmail() {
        Long userId = 1L;

        // No need to mock a Notification object since the service returns void
        doNothing().when(notificationService).sendLoanApprovalNotification(userId);

        ResponseEntity<String> response = notificationController.sendLoanApprovalNotification(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Loan approval notification sent successfully.", response.getBody());
        verify(notificationService).sendLoanApprovalNotification(userId);
    }


    @Test
    void testSendLoanApprovalNotification() {
        Long userId = 1L;

        ResponseEntity<String> response = notificationController.sendLoanApprovalNotification(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Loan approval notification sent successfully.", response.getBody());
        verify(notificationService).sendLoanApprovalNotification(userId);
    }
}
