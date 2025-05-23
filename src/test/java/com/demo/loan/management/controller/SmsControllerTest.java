package com.demo.loan.management.controller;

import com.demo.loan.management.dto.SmsRequest;
import com.demo.loan.management.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SmsControllerTest {

    @Mock
    private SmsService smsService;

    @InjectMocks
    private SmsController smsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendSms() {
        SmsRequest request = new SmsRequest();
        request.setTo("+1234567890");
        request.setBody("Test message");

        when(smsService.sendSms(request.getTo(), request.getBody()))
                .thenReturn("SMS sent successfully");

        String response = smsController.sendSms(request);
        assertEquals("SMS sent successfully", response);
        verify(smsService, times(1)).sendSms(request.getTo(), request.getBody());
    }
}
