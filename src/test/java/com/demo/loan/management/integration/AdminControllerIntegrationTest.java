package com.demo.loan.management.integration;



import com.demo.loan.management.dto.LoginRequestDTO;

import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up users
        userRepository.deleteAll();

        // Add admin user
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("adminPassword"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Add normal user
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("userPassword"));
        user.setRole(Role.USER);
        userRepository.save(user);

        // Get tokens
        adminToken = obtainAccessToken("admin@example.com", "adminPassword");
        userToken = obtainAccessToken("user@example.com", "userPassword");
    }

    @Test
    void testAdminDashboard_Success() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Welcome Admin!"));
    }

    @Test
    void testAdminDashboard_Forbidden_ForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Create a user to delete
        User tempUser = new User();
        tempUser.setEmail("delete.me@example.com");
        tempUser.setPassword(passwordEncoder.encode("test123"));
        tempUser.setRole(Role.USER);
        Long userIdToDelete = userRepository.save(tempUser).getUserId();

        mockMvc.perform(delete("/api/admin/delete/" + userIdToDelete)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void testDeleteUser_Forbidden_ForNonAdmin() throws Exception {
        User tempUser = new User();
        tempUser.setEmail("cantdelete@example.com");
        tempUser.setPassword(passwordEncoder.encode("test123"));
        tempUser.setRole(Role.USER);
        Long userId = userRepository.save(tempUser).getUserId();

        mockMvc.perform(delete("/api/admin/delete/" + userId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("token").asText();
    }
}
