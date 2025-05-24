package id.ac.ui.cs.advprog.orderservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthChecker.class);

    @Autowired
    private JdbcTemplate jdbcTemplate; // Requires a DataSource bean to be configured

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        String url = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String driver = environment.getProperty("spring.datasource.driver-class-name");
        
        logger.info("--- Database Health Check --- ");
        logger.info("Using Datasource URL: {}", url);
        logger.info("Using Datasource Username: {}", username);
        logger.info("Using JDBC Driver: {}", driver);

        try {
            // Attempt a simple query to check connectivity
            String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
            String dbVersion = jdbcTemplate.queryForObject("SELECT version()", String.class);
            logger.info("Successfully connected to database: {}", dbName);
            logger.info("Database version: {}", dbVersion);
            
            // TODO: Add a check for essential tables if needed, e.g., orders, order_items
            // Example: jdbcTemplate.execute("SELECT COUNT(*) FROM orders");
            // logger.info("Table 'orders' is accessible.");

        } catch (Exception e) {
            logger.error("Error connecting to database or performing health check: {}", e.getMessage());
            logger.error("Please ensure your PostgreSQL database is configured correctly, accessible, and the schema (e.g., 'orders' table) exists if ddl-auto is not 'create' or 'create-drop'.");
            // Consider logging e.getStackTrace() for more detailed debugging if issues persist
        }
        logger.info("--- Database Health Check Complete --- ");
    }
} 