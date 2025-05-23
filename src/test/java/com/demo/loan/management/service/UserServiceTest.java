package com.demo.loan.management.service;

import com.demo.loan.management.model.Role;
import com.demo.loan.management.model.User;
import com.demo.loan.management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteUser_AsAdmin() {
        // Given
        User user = new User();
        user.setEmail("admin@example.com");
        user.setRole(Role.ADMIN);

        // Mock UserDetails as Spring Security expects
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails adminDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), "password", authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(adminDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotAdmin() {
        // Given
        User user = new User();
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        // Mock UserDetails
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), "password", authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        SecurityException exception = assertThrows(SecurityException.class, () -> userService.deleteUser(1L));

        assertEquals("Only admins can delete users", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Given
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(Role.ADMIN);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        UserDetails adminDetails = new org.springframework.security.core.userdetails.User(
                adminUser.getEmail(), "password", authorities
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(adminDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.existsById(1L)).thenReturn(false);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(1L));

        assertEquals("User with ID 1 does not exist.", exception.getMessage());
        verify(userRepository, never()).deleteById(anyLong());
    }
}
