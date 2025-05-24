package id.ac.ui.cs.advprog.orderservice.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class EnvLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Map<String, Object> envMap = new HashMap<>();
            
            // Try to get environment variables from system env first
            String pgHost = System.getenv("PGHOST");
            String pgDatabase = System.getenv("PGDATABASE");
            String pgUser = System.getenv("PGUSER");
            String pgPassword = System.getenv("PGPASSWORD");
            
            // Only set if they exist
            if (pgHost != null) envMap.put("PGHOST", pgHost);
            if (pgDatabase != null) envMap.put("PGDATABASE", pgDatabase);
            if (pgUser != null) envMap.put("PGUSER", pgUser);
            if (pgPassword != null) envMap.put("PGPASSWORD", pgPassword);
            
            // Only add the property source if we have any entries
            if (!envMap.isEmpty()) {
                ConfigurableEnvironment environment = applicationContext.getEnvironment();
                environment.getPropertySources().addFirst(new MapPropertySource("envProperties", envMap));
                System.out.println("Environment variables loaded successfully!");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading environment variables: " + e.getMessage());
        }
    }
}