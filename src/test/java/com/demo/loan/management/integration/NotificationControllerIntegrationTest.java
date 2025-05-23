package com.demo.loan.management.integration;

import com.demo.loan.management.config.MockMailConfig;
import com.demo.loan.management.model.Notification;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.NotificationRepository;
import com.demo.loan.management.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(MockMailConfig.class)

@Transactional
public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("Password123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        testNotification = Notification.builder()
                .user(testUser)
                .notificationType("EMAIL")
                .message("Test notification message")
                .status("SENT")
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(testNotification);
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        String loginJson = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = loginResult.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(responseString);
        assertThat(node.has("token")).isTrue();
        return node.get("token").asText();
    }

    @Test
    void testCreateNotification() throws Exception {
        String token = obtainAccessToken(testUser.getEmail(), "Password123");

        Notification newNotification = Notification.builder()
                .notificationType("SMS")
                .message("New test notification")
                .status("PENDING")
                .build();

        String json = objectMapper.writeValueAsString(newNotification);

        MvcResult result = mockMvc.perform(post("/api/notifications/user/" + testUser.getUserId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());

        Notification created = objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);
        assertThat(created.getMessage()).isEqualTo("New test notification");
        assertThat(created.getNotificationType()).isEqualTo("SMS");
    }

    @Test
    void testGetAllNotifications() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<Notification> notifications = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Notification.class));
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getMessage()).isEqualTo(testNotification.getMessage());
    }

    @Test
    void testGetNotificationsByUserId() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(get("/api/notifications/user/" + testUser.getUserId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        List<Notification> notifications = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Notification.class));
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.get(0).getUser().getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    void testGetNotificationById() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(get("/api/notifications/" + testNotification.getNotificationId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        Notification notification = objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);
        assertThat(notification.getNotificationId()).isEqualTo(testNotification.getNotificationId());
    }

    @Test
    void testUpdateNotification() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        testNotification.setMessage("Updated message");
        String json = objectMapper.writeValueAsString(testNotification);

        MvcResult result = mockMvc.perform(put("/api/notifications/" + testNotification.getNotificationId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        Notification updated = objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);
        assertThat(updated.getMessage()).isEqualTo("Updated message");
    }

    @Test
    void testDeleteNotification() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        mockMvc.perform(delete("/api/notifications/" + testNotification.getNotificationId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        assertThat(notificationRepository.findById(testNotification.getNotificationId())).isEmpty();
    }

    @Test
    void testSendLoanApprovalEmail_ReturnsNotification() throws Exception {
        String token = obtainAccessToken(testUser.getEmail(), "Password123");

        MvcResult result = mockMvc.perform(post("/api/notifications/loan-approval/" + testUser.getUserId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());

        Notification sentNotification = objectMapper.readValue(result.getResponse().getContentAsString(), Notification.class);
        assertThat(sentNotification.getUser().getUserId()).isEqualTo(testUser.getUserId());
        assertThat(sentNotification.getNotificationType()).isEqualTo("Loan Approval");
    }


    @Test
    void testSendLoanApprovalNotification_ReturnsSuccessMessage() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(post("/api/notifications/send-loan-approval/" + testUser.getUserId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("Loan approval notification sent successfully");
    }
}
