package com.demo.loan.management.integration;

import com.demo.loan.management.dto.EmiPaymentRequestDTO;
import com.demo.loan.management.model.Emi;
import com.demo.loan.management.model.Loan;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.EmiRepository;
import com.demo.loan.management.repository.LoanRepository;
import com.demo.loan.management.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EmiControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private EmiRepository emiRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User user;
    private Loan loan;
    private Emi emi;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("Password123"))
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build());

        loan = loanRepository.save(Loan.builder()
                .user(user)
                .loanAmount(BigDecimal.valueOf(50000))
                .interestRate(BigDecimal.valueOf(10))
                .loanTenure(12)
                .loanStatus("APPROVED")
                .loanType("Personal")
                .createdAt(LocalDateTime.now())
                .build());

        emi = emiRepository.save(Emi.builder()
                .loan(loan)
                .emiAmount(BigDecimal.valueOf(4500))
                .dueDate(LocalDateTime.now().plusDays(10))
                .status("PENDING")
                .paymentStatus("DUE")
                .build());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testPayEmiSuccessfully() throws Exception {
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setEmiAmount(BigDecimal.valueOf(4500));
        dto.setPaymentMethod("CARD");
        dto.setMonth(5);
        dto.setYear(2025);

        mockMvc.perform(put("/api/emis/pay/" + emi.getEmiId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testPayEmiTwiceShouldFail() throws Exception {
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setEmiAmount(BigDecimal.valueOf(4500));
        dto.setPaymentMethod("CARD");
        dto.setMonth(5);
        dto.setYear(2025);

        // First payment
        mockMvc.perform(put("/api/emis/pay/" + emi.getEmiId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());

        // Second payment (should fail)
        mockMvc.perform(put("/api/emis/pay/" + emi.getEmiId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testCreateEmi() throws Exception {
        EmiPaymentRequestDTO dto = new EmiPaymentRequestDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setEmiAmount(BigDecimal.valueOf(4500));
        dto.setMonth(5);
        dto.setYear(2025);

        mockMvc.perform(post("/api/emis/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testGetEmisByLoanId() throws Exception {
        mockMvc.perform(get("/api/emis/loan/" + loan.getLoanId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void testGetEmiHistoryForUser() throws Exception {
        mockMvc.perform(get("/api/emis/history"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
