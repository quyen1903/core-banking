package com.quinnbank.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    @Profile({"dev", "test"})
    SecurityFilterChain localApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    @Bean
    @Profile({"dev", "test"})
    InMemoryUserDetailsManager localBootstrapUserDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${QUINNBANK_SECURITY_LOCAL_BOOTSTRAP_USERNAME:${quinnbank.security.local-bootstrap.username:local-admin}}") String username,
            @Value("${QUINNBANK_SECURITY_LOCAL_BOOTSTRAP_PASSWORD:${quinnbank.security.local-bootstrap.password:replace-me-local-only}}") String password,
            @Value("${QUINNBANK_SECURITY_LOCAL_BOOTSTRAP_AUTHORITIES:${quinnbank.security.local-bootstrap.authorities:EMPLOYEE_CREATE,IDENTITY_ACCOUNT_CREATE}}") String authorities
    ) {
        return new InMemoryUserDetailsManager(org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(passwordEncoder.encode(password))
                .authorities(Arrays.stream(authorities.split(","))
                        .map(String::trim)
                        .filter(authority -> !authority.isBlank())
                        .map(SimpleGrantedAuthority::new)
                        .toList())
                .build());
    }
}
