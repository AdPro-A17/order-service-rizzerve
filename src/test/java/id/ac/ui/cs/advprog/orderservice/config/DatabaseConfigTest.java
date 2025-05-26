package id.ac.ui.cs.advprog.orderservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DatabaseConfigTest {

    private DatabaseConfig databaseConfig;

    @BeforeEach
    void setUp() {
        databaseConfig = new DatabaseConfig();
    }

    @Test
    void testDataSourceWithNeonEnvironmentVariables() {
        // Arrange - Set Neon-specific environment variables
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", "test-host.neon.tech");
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "test-db");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", "test-user");
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", "test-password");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", null);

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof org.springframework.jdbc.datasource.DriverManagerDataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertTrue(driverManagerDataSource.getUrl().contains("test-host.neon.tech"));
        assertTrue(driverManagerDataSource.getUrl().contains("test-db"));
        assertEquals("test-user", driverManagerDataSource.getUsername());
        assertEquals("test-password", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceWithSpringDatasourceUrl() {
        // Arrange - Set Spring datasource properties
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "jdbc:postgresql://localhost:5432/testdb");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", "spring-user");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "spring-password");

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/testdb", driverManagerDataSource.getUrl());
        assertEquals("spring-user", driverManagerDataSource.getUsername());
        assertEquals("spring-password", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceWithPartialNeonVariables() {
        // Arrange - Set only some Neon variables (should fall back to Spring properties)
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", "test-host.neon.tech");
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "test-db");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", null); // Missing user
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", "test-password");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "jdbc:postgresql://localhost:5432/fallback");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", "fallback-user");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "fallback-password");

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/fallback", driverManagerDataSource.getUrl());
        assertEquals("fallback-user", driverManagerDataSource.getUsername());
        assertEquals("fallback-password", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceWithEmptyNeonVariables() {
        // Arrange - Set empty Neon variables (should fall back to Spring properties)
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", "");
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", "");
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", "");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "jdbc:postgresql://localhost:5432/empty-fallback");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", "empty-user");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "empty-password");

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/empty-fallback", driverManagerDataSource.getUrl());
        assertEquals("empty-user", driverManagerDataSource.getUsername());
        assertEquals("empty-password", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceWithNoValidConfiguration() {
        // Arrange - Set all variables to null
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> databaseConfig.dataSource());
        assertEquals("No valid database configuration found.", exception.getMessage());
    }

    @Test
    void testDataSourceWithPartialSpringConfiguration() {
        // Arrange - Set partial Spring configuration
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "jdbc:postgresql://localhost:5432/partial");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", null); // Missing username
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "partial-password");

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> databaseConfig.dataSource());
        assertEquals("No valid database configuration found.", exception.getMessage());
    }

    @Test
    void testDataSourceWithMixedNullAndEmptyValues() {
        // Arrange - Mix of null and empty values
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", "test-user");
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "jdbc:postgresql://localhost:5432/mixed");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", "mixed-user");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "mixed-password");

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert - Should fall back to Spring configuration
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertEquals("jdbc:postgresql://localhost:5432/mixed", driverManagerDataSource.getUrl());
        assertEquals("mixed-user", driverManagerDataSource.getUsername());
        assertEquals("mixed-password", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceWithEmptySpringConfiguration() {
        // Arrange - Empty Spring configuration (empty strings don't pass the !isEmpty() check)
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", null);
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", "");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", "");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", "");

        // Act - Empty strings will be used directly in fallback, creating a DataSource with empty values
        DataSource dataSource = databaseConfig.dataSource();

        // Assert - DataSource is created but with empty values
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        assertEquals("", driverManagerDataSource.getUrl());
        assertEquals("", driverManagerDataSource.getUsername());
        assertEquals("", driverManagerDataSource.getPassword());
    }

    @Test
    void testDataSourceUrlFormatting() {
        // Arrange - Test URL formatting for Neon
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", "special-host.neon.tech");
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "special-db");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", "special-user");
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", "special-password");
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUrl", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourceUsername", null);
        ReflectionTestUtils.setField(databaseConfig, "springDatasourcePassword", null);

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        org.springframework.jdbc.datasource.DriverManagerDataSource driverManagerDataSource = 
                (org.springframework.jdbc.datasource.DriverManagerDataSource) dataSource;
        String expectedUrl = "jdbc:postgresql://special-host.neon.tech/special-db?sslmode=require";
        assertEquals(expectedUrl, driverManagerDataSource.getUrl());
        assertTrue(driverManagerDataSource.getUrl().contains("sslmode=require"));
    }

    @Test
    void testDataSourceDriverClassName() {
        // Arrange
        ReflectionTestUtils.setField(databaseConfig, "envPgHost", "test-host");
        ReflectionTestUtils.setField(databaseConfig, "envPgDatabase", "test-db");
        ReflectionTestUtils.setField(databaseConfig, "envPgUser", "test-user");
        ReflectionTestUtils.setField(databaseConfig, "envPgPassword", "test-password");

        // Act
        DataSource dataSource = databaseConfig.dataSource();

        // Assert
        assertNotNull(dataSource);
        assertInstanceOf(org.springframework.jdbc.datasource.DriverManagerDataSource.class, dataSource);
    }
} 