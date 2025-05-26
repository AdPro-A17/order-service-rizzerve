package id.ac.ui.cs.advprog.orderservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public UserDetailsService testUserDetailsService() {
        UserDetails adminUser = User.builder()
                .username("admin")
                .password("{noop}password")
                .roles("ADMIN")
                .build();

        UserDetails regularUser = User.builder()
                .username("user")
                .password("{noop}password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(adminUser, regularUser);
    }
} 