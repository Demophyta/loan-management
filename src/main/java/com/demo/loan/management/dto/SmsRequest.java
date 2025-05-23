package com.demo.loan.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Schema(description = "DTO for sending SMS messages")
public class SmsRequest {

    @Schema(description = "Recipient phone number", example = "+1234567890")
    private String to;

    @Schema(description = "Body of the SMS message", example = "Your OTP is 123456")
    private String body;
}
