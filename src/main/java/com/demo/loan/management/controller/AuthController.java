package com.demo.loan.management.controller;

import com.demo.loan.management.dto.LoginRequestDTO;
import com.demo.loan.management.dto.RegisterRequest;
import com.demo.loan.management.dto.UserDTO;
import com.demo.loan.management.model.User;
import com.demo.loan.management.security.JwtUtil;
import com.demo.loan.management.service.TokenBlacklistService;
import com.demo.loan.management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        Map<String, Object> response = new HashMap<>();

        if (!EMAIL_PATTERN.matcher(registerRequest.getEmail()).matches()) {
            return error("Invalid email format");
        }

        if (userService.existsByEmail(registerRequest.getEmail())) {
            return error("Email already registered");
        }

        // Use UserDTO to return data after registration
        UserDTO userDTO = userService.saveUser(registerRequest);
        response.put("status", "success");
        response.put("message", "User registered successfully");
        response.put("user", userDTO); // Return user DTO instead of User entity
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return error("Invalid email or password", 401);
        }

        User user = userService.getUserByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        response.put("status", "success");
        response.put("token", token);
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout a user")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return error("Invalid token format");
        }

        String token = authHeader.substring(7);
        tokenBlacklistService.blacklistToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> error(String message) {
        return error(message, 400);
    }

    private ResponseEntity<Map<String, Object>> error(String message, int statusCode) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("error", message);
        return ResponseEntity.status(statusCode).body(error);
    }
}
