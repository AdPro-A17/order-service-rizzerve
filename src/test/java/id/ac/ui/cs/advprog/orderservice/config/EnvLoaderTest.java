package id.ac.ui.cs.advprog.orderservice.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvLoaderTest {

    @Test
    void testEnvLoaderCreation() {
        // Act
        EnvLoader envLoader = new EnvLoader();

        // Assert
        assertNotNull(envLoader);
    }

    @Test
    void testEnvLoaderImplementsApplicationContextInitializer() {
        // Act
        EnvLoader envLoader = new EnvLoader();

        // Assert
        assertInstanceOf(org.springframework.context.ApplicationContextInitializer.class, envLoader);
    }
} 