package com.chillpops.config;

import com.chillpops.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // Static resources & SPA
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**", "/uploads/**", "/favicon.ico").permitAll()
                // Auth public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Public catalog and shop details for customer viewing
                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/settings/**").permitAll()
                // POS Billing public/customer access
                .requestMatchers("/api/billing/**").permitAll()
                // Protected endpoints (reports, settings update, product modifications, transactions)
                .requestMatchers(HttpMethod.POST, "/api/products/**", "/api/products/upload-image").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/products/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").permitAll()
                .requestMatchers("/api/settings/**").permitAll()
                .requestMatchers("/api/reports/**").permitAll()
                .requestMatchers("/api/transactions/**").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
