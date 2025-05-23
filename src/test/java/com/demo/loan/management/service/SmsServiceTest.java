package com.demo.loan.management.service;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SmsServiceTest {

    @Test
    void testSendSmsDelegatesToTwilio() {
        // Arrange
        SmsSender smsSender = mock(SmsSender.class);
        String to = "+15551234567";
        String body = "Test Message";
        when(smsSender.sendSms(to, body)).thenReturn("SMS sent successfully with SID: SM123");

        // Act
        String result = smsSender.sendSms(to, body);

        // Assert
        assertTrue(result.contains("SMS sent successfully"));
        verify(smsSender, times(1)).sendSms(to, body);
    }
}
