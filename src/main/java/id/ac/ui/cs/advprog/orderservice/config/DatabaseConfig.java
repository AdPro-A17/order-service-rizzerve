package id.ac.ui.cs.advprog.orderservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    
    // These @Value annotations will try to read from properties files (application.properties)
    // OR from system properties (which Dotenv sets from .env)
    // We give them a default of null so we can check if they were actually set.
    @Value("${spring.datasource.url:#{null}}")
    private String springDatasourceUrl;
    
    @Value("${spring.datasource.username:#{null}}")
    private String springDatasourceUsername;
    
    @Value("${spring.datasource.password:#{null}}")
    private String springDatasourcePassword;
    
    @Value("${PGHOST:#{null}}")
    private String envPgHost;
    
    @Value("${PGDATABASE:#{null}}")
    private String envPgDatabase;
    
    @Value("${PGUSER:#{null}}")
    private String envPgUser;
    
    @Value("${PGPASSWORD:#{null}}")
    private String envPgPassword;
    
    @Bean
    public DataSource dataSource() {
        String jdbcUrl;
        String dbUsername;
        String dbPassword;

        logger.info("Attempting to configure database connection...");
        logger.info("Values from @Value (via .env or application.properties):");
        logger.info("PGHOST from @Value: {}", envPgHost);
        logger.info("PGDATABASE from @Value: {}", envPgDatabase);
        logger.info("PGUSER from @Value: {}", envPgUser);
        logger.info("PGPASSWORD from @Value is set: {}", envPgPassword != null && !envPgPassword.isEmpty());
        logger.info("SPRING_DATASOURCE_URL from @Value: {}", springDatasourceUrl);
        logger.info("SPRING_DATASOURCE_USERNAME from @Value: {}", springDatasourceUsername);
        logger.info("SPRING_DATASOURCE_PASSWORD from @Value is set: {}", springDatasourcePassword != null && !springDatasourcePassword.isEmpty());

        // Priority 1: Neon specific environment variables (PGHOST, etc.)
        if (envPgHost != null && !envPgHost.isEmpty() &&
            envPgDatabase != null && !envPgDatabase.isEmpty() &&
            envPgUser != null && !envPgUser.isEmpty() &&
            envPgPassword != null && !envPgPassword.isEmpty()) {
            
            jdbcUrl = String.format("jdbc:postgresql://%s/%s?sslmode=require", envPgHost, envPgDatabase); // Assuming sslmode=require for Neon
            dbUsername = envPgUser;
            dbPassword = envPgPassword;
            logger.info("Using Neon-specific environment variables (PGHOST, etc.) for connection.");

        }
        // Priority 2: SPRING_DATASOURCE_URL from .env (set as system property by Dotenv) or application.properties
        else if (springDatasourceUrl != null && !springDatasourceUrl.isEmpty() &&
                 springDatasourceUsername != null && !springDatasourceUsername.isEmpty() &&
                 springDatasourcePassword != null && !springDatasourcePassword.isEmpty()) {
            
            jdbcUrl = springDatasourceUrl;
            dbUsername = springDatasourceUsername;
            dbPassword = springDatasourcePassword;
            logger.info("Using SPRING_DATASOURCE_URL (from .env or application.properties) for connection.");

        }
        // Fallback: This case should ideally not be reached if .env is correct
        else {
            logger.warn("Neon specific (PGHOST, etc.) or SPRING_DATASOURCE_URL variables not fully set in .env or properties. Falling back to default application.properties (likely localhost). THIS IS PROBABLY NOT WHAT YOU WANT FOR PRODUCTION/NEON.");
            // Use the @Value injected properties which would be from application.properties if not overridden by .env
            jdbcUrl = this.springDatasourceUrl; // This would be the localhost one from application.properties
            dbUsername = this.springDatasourceUsername;
            dbPassword = this.springDatasourcePassword;
            if (jdbcUrl == null || dbUsername == null || dbPassword == null) {
                logger.error("CRITICAL: No valid database configuration found! Please check .env and application.properties.");
                throw new IllegalStateException("No valid database configuration found.");
            }
        }

        logger.info("Final Database Configuration:");
        logger.info("URL: {}", jdbcUrl);
        logger.info("Username: {}", dbUsername);
        logger.info("Password provided: {}", dbPassword != null && !dbPassword.isEmpty());

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver"); // Already in application.properties but good to be explicit
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);

        return dataSource;
    }
} 