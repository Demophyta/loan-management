package com.demo.loan.management.integration;

import com.demo.loan.management.config.RateLimitingFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestRateLimitingConfig {

    @Bean
    @Primary
    public RateLimitingFilter rateLimitingFilter() {

        return mock(RateLimitingFilter.class);
    }
}
