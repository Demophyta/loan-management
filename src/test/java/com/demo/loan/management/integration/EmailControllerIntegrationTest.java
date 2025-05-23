package com.demo.loan.management.integration;

import com.demo.loan.management.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles("test")
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailService emailService;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailService);
    }

    @Test
    void testSendEmail_Success() throws Exception {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        doNothing().when(emailService).sendEmail(to, subject, body);

        mockMvc.perform(post("/api/email/send")
                        .param("to", to)
                        .param("subject", subject)
                        .param("body", body))
                .andExpect(status().isOk())
                .andExpect(content().string("Email sent successfully!"));
    }

    @Test
    void testSendEmail_Failure() throws Exception {
        String to = "fail@example.com";
        String subject = "Error";
        String body = "This should fail";

        doThrow(new RuntimeException("Email sending failed"))
                .when(emailService).sendEmail(to, subject, body);

        mockMvc.perform(post("/api/email/send")
                        .param("to", to)
                        .param("subject", subject)
                        .param("body", body))
                .andExpect(status().isOk())
                .andExpect(content().string("An error occurred while sending email."));
    }

    @TestConfiguration
    static class MockEmailServiceConfig {

        @Bean
        @Primary
        public EmailService mockEmailService() {
            return Mockito.mock(EmailService.class);
        }
    }
}
