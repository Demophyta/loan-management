package com.demo.loan.management.integration;

import com.demo.loan.management.dto.LoanRequestDTO;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.LoanRepository;
import com.demo.loan.management.repository.UserRepository;
import com.demo.loan.management.service.LoanService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
        userRepository.deleteAll();

        // Create a normal user
        testUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("Password123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        // Create an admin user for testing secured endpoints
        User adminUser = User.builder()
                .email("amadeosun00@gmail.com")
                .password(passwordEncoder.encode("123456"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();
        userRepository.save(adminUser);
    }

    // Helper method to extract token from login JSON response
    private String extractTokenFromJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.get("token").asText();  // Ensure your login response has field "token"
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract JWT token from response", e);
        }
    }

    @Test
    void testApplyLoan_Success() {
        try {
            LoanRequestDTO loanRequestDTO = LoanRequestDTO.builder()
                    .loanAmount(new BigDecimal("5000"))
                    .interestRate(new BigDecimal("5.0"))
                    .loanType("PERSONAL")
                    .userId(testUser.getUserId())
                    .loanTenure(12)
                    .build();

            MvcResult result = mockMvc.perform(post("/api/loans/apply")
                            .with(SecurityMockMvcRequestPostProcessors.user(testUser.getEmail()).roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loanRequestDTO)))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("Apply Loan Response Status: " + result.getResponse().getStatus());
            System.out.println("Apply Loan Response Content: " + result.getResponse().getContentAsString());

        } catch (Exception e) {
            System.err.println("Exception in testApplyLoan_Success:");
            e.printStackTrace();
        }
    }

    @Test
    void testGetAllLoansAsAdmin() {
        try {
            // Step 1: Login and extract token
            String loginRequestJson = "{ \"email\": \"amadeosun00@gmail.com\", \"password\": \"123456\" }";

            MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseBody = loginResult.getResponse().getContentAsString();
            String adminJwtToken = extractTokenFromJson(responseBody);  // Extract token

            // Step 2: Access secured endpoint with token
            MvcResult result = mockMvc.perform(get("/api/loans/all")
                            .header("Authorization", "Bearer " + adminJwtToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("Get All Loans Admin Response Status: " + result.getResponse().getStatus());
            System.out.println("Get All Loans Admin Response Content: " + result.getResponse().getContentAsString());

        } catch (Exception e) {
            System.err.println("Exception in testGetAllLoansAsAdmin:");
            e.printStackTrace();
        }
    }

    @Test
    void testGetLoansByUser() {
        try {
            Loan loan = Loan.builder()
                    .loanAmount(new BigDecimal("5000"))
                    .interestRate(new BigDecimal("5.0"))
                    .loanTenure(12)
                    .user(testUser)
                    .build();
            loanRepository.save(loan);

            MvcResult result = mockMvc.perform(get("/api/loans/user/{userId}", testUser.getUserId())
                            .with(SecurityMockMvcRequestPostProcessors.user(testUser.getEmail()).roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("Get Loans By User Response Status: " + result.getResponse().getStatus());
            System.out.println("Get Loans By User Response Content: " + result.getResponse().getContentAsString());

        } catch (Exception e) {
            System.err.println("Exception in testGetLoansByUser:");
            e.printStackTrace();
        }
    }

    @Test
    void testApproveLoan_AsAdmin() {
        try {
            Loan loan = Loan.builder()
                    .loanAmount(new BigDecimal("5000"))
                    .interestRate(new BigDecimal("5.0"))
                    .loanTenure(12)
                    .user(testUser)
                    .build();
            loanRepository.save(loan);

            MvcResult result = mockMvc.perform(put("/api/loans/{loanId}/approve", loan.getLoanId())
                            .with(SecurityMockMvcRequestPostProcessors.user("amadeosun00@gmail.com").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("Approve Loan Response Status: " + result.getResponse().getStatus());
            System.out.println("Approve Loan Response Content: " + result.getResponse().getContentAsString());

        } catch (Exception e) {
            System.err.println("Exception in testApproveLoan_AsAdmin:");
            e.printStackTrace();
        }
    }

    @Test
    void testGetLoanByIdAsUser() {
        try {
            Loan loan = Loan.builder()
                    .loanAmount(new BigDecimal("5000"))
                    .interestRate(new BigDecimal("5.0"))
                    .loanTenure(12)
                    .user(testUser)
                    .build();
            loanRepository.save(loan);

            MvcResult result = mockMvc.perform(get("/api/loans/{loanId}", loan.getLoanId())
                            .with(SecurityMockMvcRequestPostProcessors.user(testUser.getEmail()).roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            System.out.println("Get Loan By ID Response Status: " + result.getResponse().getStatus());
            System.out.println("Get Loan By ID Response Content: " + result.getResponse().getContentAsString());

        } catch (Exception e) {
            System.err.println("Exception in testGetLoanByIdAsUser:");
            e.printStackTrace();
        }
    }
}
