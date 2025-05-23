package com.demo.loan.management.controller;

import com.demo.loan.management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdminDashboard() {
        // Act
        ResponseEntity<?> response = adminController.adminDashboard();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Welcome Admin!", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long userId = 5L;
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<?> response = adminController.deleteUser(userId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("User deleted successfully", ((Map<?, ?>) Objects.requireNonNull(response.getBody())).get("message"));
        verify(userService, times(1)).deleteUser(userId);
    }
}
