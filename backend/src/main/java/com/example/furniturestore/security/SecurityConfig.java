package com.example.furniturestore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import com.example.furniturestore.security.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    public SecurityConfig(JwtFilter jwtFilter) {
    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(JwtFilter jwtFilter, ApiKeyFilter apiKeyFilter) {

        this.jwtFilter = jwtFilter;
        this.apiKeyFilter = apiKeyFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
                        .requestMatchers("/api/auth/**", "/api/categories", "/uploads/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
