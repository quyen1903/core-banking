package com.quinnbank.core.auth.adapter.out.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/** Temporary unauthenticated access for isolated local development with synthetic data only. */
@Configuration(proxyBeanMethods = false)
@Profile("local & !dev & !test & !qa & !uat & !staging & !pre-prod & !prod")
public class LocalDevelopmentSecurityConfiguration {

    @Bean
    SecurityFilterChain localDevelopmentSecurityFilterChain(HttpSecurity http, Environment environment) throws Exception {
        String serverAddress = environment.getProperty("server.address");
        if (!"127.0.0.1".equals(serverAddress) && !"::1".equals(serverAddress)) {
            throw new IllegalStateException(
                    "Local development security requires server.address=127.0.0.1 or ::1");
        }

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }
}
