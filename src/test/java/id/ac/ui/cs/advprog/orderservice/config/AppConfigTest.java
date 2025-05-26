package id.ac.ui.cs.advprog.orderservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testRestTemplateBean() {
        // Arrange
        AppConfig appConfig = new AppConfig();

        // Act
        RestTemplate restTemplate = appConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate);
        assertInstanceOf(RestTemplate.class, restTemplate);
    }
} 