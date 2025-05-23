package com.demo.loan.management.service;

import com.demo.loan.management.model.BlacklistedToken;
import com.demo.loan.management.repository.BlacklistedTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBlacklistToken_SavesToken() {
        String token = "sample.jwt.token";

        tokenBlacklistService.blacklistToken(token);

        verify(blacklistedTokenRepository, times(1)).save(argThat(savedToken ->
                savedToken.getToken().equals(token) && savedToken.getBlacklistedAt() != null));
    }

    @Test
    void testIsTokenBlacklisted_ReturnsTrueIfPresent() {
        String token = "blacklisted.jwt.token";

        when(blacklistedTokenRepository.findByToken(token)).thenReturn(Optional.of(new BlacklistedToken()));

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertTrue(result);
        verify(blacklistedTokenRepository).findByToken(token);
    }

    @Test
    void testIsTokenBlacklisted_ReturnsFalseIfNotPresent() {
        String token = "valid.jwt.token";

        when(blacklistedTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertFalse(result);
        verify(blacklistedTokenRepository).findByToken(token);
    }
}
