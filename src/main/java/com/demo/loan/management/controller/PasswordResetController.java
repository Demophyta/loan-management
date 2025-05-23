package com.demo.loan.management.controller;

import com.demo.loan.management.dto.PasswordResetEmailRequestDto;
import com.demo.loan.management.dto.PasswordResetOtpRequestDto;
import com.demo.loan.management.dto.PasswordResetOtpResetRequestDto;
import com.demo.loan.management.dto.PasswordResetTokenDto;
import com.demo.loan.management.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "Request password reset via email", description = "Initiate password reset by sending a reset link to the provided email address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent to email"),
            @ApiResponse(responseCode = "400", description = "Invalid email address provided")
    })
    @PostMapping("/email")
    public ResponseEntity<String> requestPasswordResetByEmail(@RequestBody PasswordResetEmailRequestDto request) {
        passwordResetService.initiateResetByEmail(request.getEmail());
        return ResponseEntity.ok("Password reset link sent to email.");
    }

    @Operation(summary = "Request password reset OTP", description = "Initiate password reset by sending an OTP to the provided phone number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP sent to mobile number"),
            @ApiResponse(responseCode = "400", description = "Invalid phone number provided")
    })
    @PostMapping("/otp")
    public ResponseEntity<String> requestPasswordResetByOtp(@RequestBody PasswordResetOtpRequestDto request) {
        passwordResetService.initiateResetByOTP(request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent to mobile number.");
    }

    @Operation(summary = "Reset password using token", description = "Reset the user's password using the password reset token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetTokenDto request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }

    @Operation(summary = "Reset password using OTP", description = "Reset the user's password by verifying the OTP sent to the user's phone number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password successfully reset"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP provided")
    })
    @PostMapping("/reset-otp")
    public ResponseEntity<String> resetPasswordWithOtp(@RequestBody PasswordResetOtpResetRequestDto request) {
        passwordResetService.resetPasswordWithOTP(request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok("Password successfully reset.");
    }
}
