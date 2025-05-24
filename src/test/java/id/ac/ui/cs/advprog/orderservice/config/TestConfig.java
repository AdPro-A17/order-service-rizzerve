package id.ac.ui.cs.advprog.orderservice.config;

import id.ac.ui.cs.advprog.orderservice.client.TableServiceClient;
import id.ac.ui.cs.advprog.orderservice.client.MenuServiceClient;
import id.ac.ui.cs.advprog.orderservice.observer.OrderEventPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    @Primary
    public TableServiceClient tableServiceClient() {
        return mock(TableServiceClient.class);
    }

    @Bean
    @Primary
    public MenuServiceClient menuServiceClient() {
        return mock(MenuServiceClient.class);
    }

    @Bean
    @Primary
    public OrderEventPublisher orderEventPublisher() {
        return mock(OrderEventPublisher.class);
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return mock(RestTemplate.class);
    }
} 