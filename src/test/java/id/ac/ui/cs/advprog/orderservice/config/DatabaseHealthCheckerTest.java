package id.ac.ui.cs.advprog.orderservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthCheckerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Environment environment;

    @InjectMocks
    private DatabaseHealthChecker databaseHealthChecker;

    @BeforeEach
    void setUp() {
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:postgresql://localhost:5432/testdb");
        when(environment.getProperty("spring.datasource.username")).thenReturn("testuser");
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn("org.postgresql.Driver");
    }

    @Test
    void testRunWithSuccessfulDatabaseConnection() throws Exception {
        // Arrange
        when(jdbcTemplate.queryForObject("SELECT current_database()", String.class)).thenReturn("testdb");
        when(jdbcTemplate.queryForObject("SELECT version()", String.class)).thenReturn("PostgreSQL 13.0");

        // Act & Assert - Should not throw any exception
        assertDoesNotThrow(() -> databaseHealthChecker.run());

        // Verify interactions
        verify(jdbcTemplate, times(1)).queryForObject("SELECT current_database()", String.class);
        verify(jdbcTemplate, times(1)).queryForObject("SELECT version()", String.class);
        verify(environment, times(1)).getProperty("spring.datasource.url");
        verify(environment, times(1)).getProperty("spring.datasource.username");
        verify(environment, times(1)).getProperty("spring.datasource.driver-class-name");
    }

    @Test
    void testRunWithDatabaseConnectionFailure() throws Exception {
        // Arrange
        when(jdbcTemplate.queryForObject("SELECT current_database()", String.class))
                .thenThrow(new DataAccessException("Connection failed") {});

        // Act & Assert - Should not throw any exception (error is caught and logged)
        assertDoesNotThrow(() -> databaseHealthChecker.run());

        // Verify interactions
        verify(jdbcTemplate, times(1)).queryForObject("SELECT current_database()", String.class);
        verify(jdbcTemplate, never()).queryForObject("SELECT version()", String.class);
        verify(environment, times(1)).getProperty("spring.datasource.url");
        verify(environment, times(1)).getProperty("spring.datasource.username");
        verify(environment, times(1)).getProperty("spring.datasource.driver-class-name");
    }

    @Test
    void testRunWithNullEnvironmentProperties() throws Exception {
        // Arrange
        when(environment.getProperty("spring.datasource.url")).thenReturn(null);
        when(environment.getProperty("spring.datasource.username")).thenReturn(null);
        when(environment.getProperty("spring.datasource.driver-class-name")).thenReturn(null);
        when(jdbcTemplate.queryForObject("SELECT current_database()", String.class)).thenReturn("testdb");
        when(jdbcTemplate.queryForObject("SELECT version()", String.class)).thenReturn("PostgreSQL 13.0");

        // Act & Assert - Should not throw any exception
        assertDoesNotThrow(() -> databaseHealthChecker.run());

        // Verify interactions
        verify(environment, times(1)).getProperty("spring.datasource.url");
        verify(environment, times(1)).getProperty("spring.datasource.username");
        verify(environment, times(1)).getProperty("spring.datasource.driver-class-name");
    }

    @Test
    void testRunWithPartialDatabaseFailure() throws Exception {
        // Arrange - First query succeeds, second fails
        when(jdbcTemplate.queryForObject("SELECT current_database()", String.class)).thenReturn("testdb");
        when(jdbcTemplate.queryForObject("SELECT version()", String.class))
                .thenThrow(new DataAccessException("Version query failed") {});

        // Act & Assert - Should not throw any exception (error is caught and logged)
        assertDoesNotThrow(() -> databaseHealthChecker.run());

        // Verify interactions
        verify(jdbcTemplate, times(1)).queryForObject("SELECT current_database()", String.class);
        verify(jdbcTemplate, times(1)).queryForObject("SELECT version()", String.class);
    }

    @Test
    void testRunWithRuntimeException() throws Exception {
        // Arrange
        when(jdbcTemplate.queryForObject("SELECT current_database()", String.class))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert - Should not throw any exception (error is caught and logged)
        assertDoesNotThrow(() -> databaseHealthChecker.run());

        // Verify interactions
        verify(jdbcTemplate, times(1)).queryForObject("SELECT current_database()", String.class);
        verify(jdbcTemplate, never()).queryForObject("SELECT version()", String.class);
    }
} 