package id.ac.ui.cs.advprog.orderservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        // Load .env file before Spring Boot starts
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            dotenv.entries().forEach(e -> {
                System.setProperty(e.getKey(), e.getValue());
            });
            System.out.println(".env file loaded successfully and properties set.");
        } catch (Exception e) {
            System.err.println("Error loading .env file: " + e.getMessage());
            // Optionally, rethrow or handle more gracefully if .env is critical
        }

        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
