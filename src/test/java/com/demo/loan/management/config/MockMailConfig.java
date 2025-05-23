package com.demo.loan.management.config;

import jakarta.mail.internet.MimeMessage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

@TestConfiguration
public class MockMailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        // Create a mock mail sender
        JavaMailSender mockSender = Mockito.mock(JavaMailSender.class);

        // Prevent real email sending
        Mockito.doNothing().when(mockSender).send(Mockito.any(MimeMessage.class));

        return mockSender;
    }
}
