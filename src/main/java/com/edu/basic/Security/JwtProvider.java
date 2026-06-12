package com.edu.basic.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret:your-secret-key-change-this-in-production-minimum-32-characters}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    /**
     * Generate JWT token
     * @param userId User ID
     * @param email User email
     * @return JWT token
     */
    public String generateToken(Long userId, String email) {
        try {
            log.debug("Generating token for user: {} with email: {}", userId, email);

            // Ensure secret key is at least 32 characters for HS512
            if (jwtSecret == null || jwtSecret.length() < 32) {
                log.error("JWT secret key is too short. Minimum 32 characters required.");
                throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
            }

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("userId", userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            log.info("Token generated successfully for user: {}", email);
            return token;

        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token: " + e.getMessage());
        }
    }

    /**
     * Get email from JWT token
     * @param token JWT token
     * @return Email from token
     */
    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (Exception e) {
            log.error("Error extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract email from token: " + e.getMessage());
        }
    }

    /**
     * Get userId from JWT token
     * @param token JWT token
     * @return User ID from token
     */
    public Long getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);

        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract userId from token: " + e.getMessage());
        }
    }

    /**
     * Validate JWT token
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            log.debug("Token validation successful");
            return true;

        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
}