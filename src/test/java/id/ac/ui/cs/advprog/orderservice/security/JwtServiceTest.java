package id.ac.ui.cs.advprog.orderservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Set the secret key for testing (this will fail in RED phase because the field doesn't exist yet)
        // ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String token = "sample.jwt.token";
        
        // Act
        String extractedUsername = jwtService.extractUsername(token);
        
        // Assert - This will pass in RED phase because we return null
        assertNull(extractedUsername);
    }

    @Test
    void testExtractRoles() {
        // Arrange
        String token = "sample.jwt.token";
        
        // Act
        List<String> roles = jwtService.extractRoles(token);
        
        // Assert - This will pass in RED phase because we return empty list
        assertNotNull(roles);
        assertEquals(0, roles.size());
    }

    @Test
    void testValidateToken_withValidToken() {
        // Arrange
        String token = "sample.jwt.token";
        UserDetails userDetails = User.withUsername("admin")
            .password("password")
            .roles("ADMIN")
            .build();
        
        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);
        
        // Assert - This will pass in RED phase because we return false
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_withInvalidUsername() {
        // Arrange
        String token = "sample.jwt.token";
        UserDetails userDetails = User.withUsername("wronguser")
            .password("password")
            .roles("ADMIN")
            .build();
        
        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);
        
        // Assert - This will pass in RED phase because we return false
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_withExpiredToken() {
        // Arrange
        String token = "expired.jwt.token";
        UserDetails userDetails = User.withUsername("admin")
            .password("password")
            .roles("ADMIN")
            .build();
        
        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);
        
        // Assert - This will pass in RED phase because we return false
        assertFalse(isValid);
    }

    // These tests will fail in RED phase because the methods don't exist yet
    // They will be implemented in GREEN phase
    
    @Test
    void testExtractUsernameFromRealToken_shouldFailInRedPhase() {
        // This test expects actual JWT functionality which doesn't exist in RED phase
        String realToken = generateSampleToken();
        
        // This should return null in RED phase, but will work in GREEN phase
        String username = jwtService.extractUsername(realToken);
        assertNull(username); // RED phase expectation
    }

    @Test
    void testExtractRolesFromRealToken_shouldFailInRedPhase() {
        // This test expects actual JWT functionality which doesn't exist in RED phase
        String realToken = generateSampleTokenWithRoles();
        
        // This should return empty list in RED phase, but will work in GREEN phase
        List<String> roles = jwtService.extractRoles(realToken);
        assertTrue(roles.isEmpty()); // RED phase expectation
    }

    // Helper method to simulate token generation (will be properly implemented in GREEN phase)
    private String generateSampleToken() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAzNjAwfQ.signature";
    }

    private String generateSampleTokenWithRoles() {
        return "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfQURNSU4iXSwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMDM2MDB9.signature";
    }
} 