package com.demo.loan.management.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.loan.management.model.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class JwtUtil {

    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    private final SecretKey secretKey;
    private final long jwtExpirationInMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms:3600000}") long jwtExpirationInMs) { // default 1 hour
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername != null &&
                extractedUsername.equals(username) &&
                !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaims(token).map(Claims::getSubject).orElse(null);
    }

    public Role extractUserRole(String token) {
        return extractClaims(token)
                .map(claims -> claims.get("role", String.class))
                .map(roleStr -> {
                    try {
                        return Role.valueOf(roleStr);
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "Invalid role in JWT token: " + roleStr, e);
                        return null;
                    }
                })
                .orElse(null);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .map(expiration -> expiration.before(new Date()))
                .orElse(true);
    }

    private Optional<Date> extractExpiration(String token) {
        return extractClaims(token).map(Claims::getExpiration);
    }

    private Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            logger.log(Level.WARNING, "JWT token parsing error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
