package com.example.repomanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final String SCOPE_REPO_READ = "SCOPE_REPO_READ";
    public static final String SCOPE_REPO_STORE = "SCOPE_REPO_STORE";
    public static final String SCOPE_REPO_DELETE = "SCOPE_REPO_DELETE";

    protected static final String[] freeEndpoints = {
            "/actuator/prometheus" // prom
    };
    protected static final String[] protectedEndpoints = {
            "/api/**" // API
    };

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain oauth2AuthChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(freeEndpoints).permitAll())
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(protectedEndpoints).authenticated())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // This code is only for test purpose...never ever put this into any productive system or you will probably lose your job
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("user")
                .password("thisIsOnlyATest")
                .roles("USER", SCOPE_REPO_READ)
                .build());
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("admin")
                .password("thisIsOnlyATest")
                .roles("ADMIN", SCOPE_REPO_READ, SCOPE_REPO_STORE, SCOPE_REPO_DELETE)
                .build());
        return manager;
    }
}
