package id.ac.ui.cs.advprog.orderservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String token = generateValidToken("admin", null);
        
        // Act
        String extractedUsername = jwtService.extractUsername(token);
        
        // Assert - GREEN phase: should extract username correctly
        assertEquals("admin", extractedUsername);
    }

    @Test
    void testExtractRoles() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = generateValidToken("admin", claims);
        
        // Act
        List<String> roles = jwtService.extractRoles(token);
        
        // Assert - GREEN phase: should extract roles correctly
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_ADMIN", roles.get(0));
    }

    @Test
    void testExtractAdminId() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", "123e4567-e89b-12d3-a456-426614174000");
        String token = generateValidToken("admin", claims);
        
        // Act
        String adminId = jwtService.extractAdminId(token);
        
        // Assert
        assertEquals("123e4567-e89b-12d3-a456-426614174000", adminId);
    }

    @Test
    void testIsTokenValid_withValidToken() {
        // Arrange
        String token = generateValidToken("admin", null);
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, "admin");
        
        // Assert - GREEN phase: should validate correctly
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_withWrongUsername() {
        // Arrange
        String token = generateValidToken("admin", null);
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, "wronguser");
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_withExpiredToken() {
        // Arrange
        String token = generateExpiredToken("admin");
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, "admin");
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractRoles_withNullRole() {
        // Arrange
        String token = generateValidToken("admin", null); // No role claim
        
        // Act
        List<String> roles = jwtService.extractRoles(token);
        
        // Assert
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    void testExtractUsername_withInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";
        
        // Act
        String username = jwtService.extractUsername(invalidToken);
        
        // Assert - Should return null for invalid token
        assertNull(username);
    }

    // Helper methods for token generation
    private String generateValidToken(String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = extraClaims != null ? extraClaims : new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateExpiredToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1 hour ago
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
} 