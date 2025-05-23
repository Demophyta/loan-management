package com.demo.loan.management.controller;

import com.demo.loan.management.dto.LoginRequestDTO;
import com.demo.loan.management.dto.LoginResponseDTO;
import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.security.JwtUtil;
import com.demo.loan.management.service.UserDetailsServiceImpl;
import com.demo.loan.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO request = new LoginRequestDTO("john@example.com", "password123");
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mock(UserDetails.class));
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(eq("john@example.com"), eq("USER"))).thenReturn("mocked-token");
        LoginResponseDTO response = userController.login(request);
        assertNotNull(response);
        assertEquals("mocked-token", response.getToken());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testLoginInvalidCredentials() {

        LoginRequestDTO request = new LoginRequestDTO("invalid@example.com", "wrongpassword");

        doThrow(new BadCredentialsException("Invalid")).when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.login(request));

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("notfound@example.com", "password");

        when(authenticationManager.authenticate(any())).thenReturn(mock(org.springframework.security.core.Authentication.class));
        when(userDetailsService.loadUserByUsername("notfound@example.com")).thenReturn(mock(UserDetails.class));
        when(userService.getUserByEmail("notfound@example.com")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.login(request));

        assertEquals("User not found", exception.getMessage());
    }
}
