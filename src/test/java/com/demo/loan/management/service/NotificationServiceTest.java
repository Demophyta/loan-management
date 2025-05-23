package com.demo.loan.management.service;

import com.demo.loan.management.model.Notification;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.NotificationRepository;
import com.demo.loan.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNotification() {
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setEmail("test@example.com");

        Notification inputNotification = new Notification();
        inputNotification.setMessage("Test message");

        Notification savedNotification = new Notification();
        savedNotification.setNotificationId(1L);
        savedNotification.setMessage("Test message");
        savedNotification.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.createNotification(userId, inputNotification);

        assertNotNull(result);
        assertEquals("Test message", result.getMessage());
        verify(emailService).sendEmail(eq("test@example.com"), eq("New Notification"), eq("Test message"));
    }

    @Test
    void testGetAllNotifications() {
        Notification n1 = new Notification();
        Notification n2 = new Notification();
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(n1, n2));

        List<Notification> list = notificationService.getAllNotifications();
        assertEquals(2, list.size());
    }

    @Test
    void testGetNotificationsByUserId() {
        Long userId = 1L;
        Notification n1 = new Notification();
        Notification n2 = new Notification();
        when(notificationRepository.findByUserUserId(userId)).thenReturn(Arrays.asList(n1, n2));

        List<Notification> list = notificationService.getNotificationsByUserId(userId);
        assertEquals(2, list.size());
    }

    @Test
    void testGetNotificationById() {
        Notification n = new Notification();
        n.setNotificationId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        Notification result = notificationService.getNotificationById(1L);
        assertEquals(1L, result.getNotificationId());
    }

    @Test
    void testUpdateNotification() {
        Long id = 1L;
        Notification existing = new Notification();
        existing.setNotificationId(id);
        existing.setMessage("Old");

        Notification updated = new Notification();
        updated.setMessage("New");
        updated.setStatus("READ");
        updated.setNotificationType("INFO");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existing));
        when(notificationRepository.save(any(Notification.class))).thenReturn(existing);

        Notification result = notificationService.updateNotification(id, updated);

        assertEquals("New", result.getMessage());
        assertEquals("READ", result.getStatus());
        assertEquals("INFO", result.getNotificationType());
    }

    @Test
    void testDeleteNotification() {
        Notification n = new Notification();
        n.setNotificationId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.deleteNotification(1L);
        verify(notificationRepository).delete(n);
    }

    @Test
    void testSendLoanApprovalNotification() {
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setEmail("loanuser@example.com");
        user.setFirstName("Alex");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setNotificationId(1L); // Simulate DB-generated ID
            return n;
        });

        Notification result = notificationService.sendLoanApprovalNotification(userId);

        assertNotNull(result);
        assertEquals("Loan Approval", result.getNotificationType());
        assertEquals("UNREAD", result.getStatus());
        assertEquals("Congratulations! Your loan has been approved.", result.getMessage());
        assertEquals(user, result.getUser());

        verify(emailService).sendEmail(
                eq("loanuser@example.com"),
                eq("Loan Approved"),
                contains("Dear Alex")
        );
    }
}
