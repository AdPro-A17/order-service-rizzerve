package id.ac.ui.cs.advprog.orderservice.config; // Assuming you want it in this package for tests

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary // Ensures this DataSource bean is used over the main one during tests
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"); // MODE=PostgreSQL for better compatibility
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
} 