package com.quinnbank.core.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
class TimeConfiguration {

    @Bean
    Clock systemClock() {
        return Clock.systemUTC();
    }
}
