package com.demo.loan.management.controller;

import com.demo.loan.management.dto.SmsRequest;
import com.demo.loan.management.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
@Tag(name = "SMS Controller", description = "Handles SMS functionality via Twilio.")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    @Operation(summary = "Send SMS", description = "Send a custom SMS to a specific phone number.")
    public String sendSms(@RequestBody SmsRequest smsRequest) {
        return smsService.sendSms(smsRequest.getTo(), smsRequest.getBody());
    }
}
