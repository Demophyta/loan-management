package com.demo.loan.management.controller;

import com.demo.loan.management.dto.LoginRequestDTO;
import com.demo.loan.management.dto.RegisterRequest;
import com.demo.loan.management.dto.UserDTO;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.security.JwtUtil;
import com.demo.loan.management.service.TokenBlacklistService;
import com.demo.loan.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        // Prepare the RegisterRequest
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .role(Role.USER)
                .build();

        // Prepare the UserDTO to be returned by the mock
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setFirstName(request.getFirstName());
        userDTO.setLastName(request.getLastName());
        userDTO.setRole(request.getRole().name());

        // Mock the behavior of userService
        when(userService.existsByEmail(request.getEmail())).thenReturn(false);
        when(userService.saveUser(any(RegisterRequest.class))).thenReturn(userDTO); // Mock UserDTO here

        // Call the method being tested (authController.register())
        ResponseEntity<Map<String, Object>> response = authController.register(request);

        // Verify the results
        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", Objects.requireNonNull(response.getBody()).get("status"));
        assertEquals(userDTO, response.getBody().get("user"));
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO request = new LoginRequestDTO("test@example.com", "password123");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);

        when(userService.getUserByEmail(request.getEmail())).thenReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name())).thenReturn("mock-token");

        ResponseEntity<Map<String, Object>> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("mock-token", Objects.requireNonNull(response.getBody()).get("token"));
        assertEquals("success", response.getBody().get("status"));
    }

    @Test
    void testLogoutSuccess() {
        // Valid logout request
        String token = "Bearer mock-token";

        // Call logout method
        ResponseEntity<Map<String, Object>> response = authController.logout(token);

        // Verify that blacklistToken method is called once
        verify(tokenBlacklistService, times(1)).blacklistToken("mock-token");

        // Assertions
        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", Objects.requireNonNull(response.getBody()).get("status"));
    }

    @Test
    void testLogoutInvalidTokenFormat() {
        // Invalid token format
        String invalidHeader = "InvalidHeader";

        // Call logout method
        ResponseEntity<Map<String, Object>> response = authController.logout(invalidHeader);

        // Assertions
        assertEquals(400, response.getStatusCode().value());
        assertEquals("error", Objects.requireNonNull(response.getBody()).get("status"));
        assertEquals("Invalid token format", response.getBody().get("error"));
    }
}
