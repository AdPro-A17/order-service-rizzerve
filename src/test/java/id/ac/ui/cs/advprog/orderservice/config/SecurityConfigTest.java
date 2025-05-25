package id.ac.ui.cs.advprog.orderservice.config;

import id.ac.ui.cs.advprog.orderservice.security.JwtAuthFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testPasswordEncoderBean() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder.encode("test").startsWith("$2a$"));
    }

    @Test
    void testUserDetailsServiceBean() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService();

        // Assert
        assertNotNull(userDetailsService);
        assertTrue(userDetailsService.loadUserByUsername("admin").getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testCorsConfigurationSourceBean() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Assert
        assertNotNull(corsConfigurationSource);
        assertTrue(corsConfigurationSource instanceof org.springframework.web.cors.UrlBasedCorsConfigurationSource);
    }

    @Test
    void testCorsConfigurationCreation() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        CorsConfigurationSource corsConfigurationSource = securityConfig.corsConfigurationSource();

        // Assert
        assertNotNull(corsConfigurationSource);
        // Test that the configuration source is properly created
        assertInstanceOf(org.springframework.web.cors.UrlBasedCorsConfigurationSource.class, corsConfigurationSource);
    }

    @Test
    void testPasswordEncoderEncryption() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Act
        String encoded1 = encoder.encode("password");
        String encoded2 = encoder.encode("password");

        // Assert
        assertNotEquals(encoded1, encoded2); // BCrypt should generate different hashes
        assertTrue(encoder.matches("password", encoded1));
        assertTrue(encoder.matches("password", encoded2));
        assertFalse(encoder.matches("wrongpassword", encoded1));
    }

    @Test
    void testUserDetailsServiceUserCreation() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService();

        // Assert
        assertNotNull(userDetailsService);
        
        // Test admin user exists and has correct properties
        var adminUser = userDetailsService.loadUserByUsername("admin");
        assertNotNull(adminUser);
        assertEquals("admin", adminUser.getUsername());
        assertTrue(adminUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(adminUser.isEnabled());
        assertTrue(adminUser.isAccountNonExpired());
        assertTrue(adminUser.isAccountNonLocked());
        assertTrue(adminUser.isCredentialsNonExpired());
    }

    @Test
    void testUserDetailsServiceNonExistentUser() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();
        UserDetailsService userDetailsService = securityConfig.userDetailsService();

        // Act & Assert
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));
    }

    @Test
    void testCorsConfigurationSourceRegistration() {
        // Arrange
        SecurityConfig securityConfig = new SecurityConfig();

        // Act
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        // Assert
        assertNotNull(source);
        assertInstanceOf(org.springframework.web.cors.UrlBasedCorsConfigurationSource.class, source);
        
        // Test that it's a UrlBasedCorsConfigurationSource with proper registration
        org.springframework.web.cors.UrlBasedCorsConfigurationSource urlSource = 
                (org.springframework.web.cors.UrlBasedCorsConfigurationSource) source;
        assertNotNull(urlSource);
    }
} 