package com.demo.loan.management.integration;

import com.demo.loan.management.model.*;
import com.demo.loan.management.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private EmiRepository emiRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User testUser;
    private Loan loan;
    private Emi emi;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        // Clear DB before each test to avoid conflicts
        transactionRepository.deleteAll();
        emiRepository.deleteAll();
        loanRepository.deleteAll();
        userRepository.deleteAll();

        // Fixed LocalDateTime to avoid inconsistencies
        LocalDateTime now = LocalDateTime.now();

        // Create test user
        testUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("Password123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        // Create loan
        loan = Loan.builder()
                .loanAmount(new BigDecimal("5000"))
                .interestRate(new BigDecimal("5.0"))
                .loanTenure(12)
                .loanStatus("APPROVED")
                .loanType("PERSONAL")
                .user(testUser)
                .build();
        loanRepository.save(loan);

        // Create EMI
        emi = Emi.builder()
                .loan(loan)
                .emiAmount(new BigDecimal("450"))
                .dueDate(now.plusDays(30))
                .status("PAID")
                .paidOn(now)
                .paymentStatus("SUCCESS")
                .build();
        emiRepository.save(emi);

        transaction = Transaction.builder()
                .emi(emi)
                .transactionAmount(emi.getEmiAmount())
                .transactionDate(now)
                .transactionStatus("SUCCESS")
                .build();
        transactionRepository.save(transaction);
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
    void testGetTransactionsByLoanId() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(get("/api/transactions/loan/" + loan.getLoanId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("Transaction History Content: " + response);

        // Basic assertion example: check the response contains the transaction ID
        assertThat(response).contains(transaction.getTransactionId().toString());
    }



    @Test
    void testGetTransactionById() throws Exception {
        String token = obtainAccessToken("user@example.com", "Password123");

        MvcResult result = mockMvc.perform(get("/api/transactions/" + transaction.getTransactionId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("Transaction Details Content: " + response);
        assertThat(response).contains(emi.getEmiAmount().toString());
    }
}
