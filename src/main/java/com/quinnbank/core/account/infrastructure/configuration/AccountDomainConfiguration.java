package com.quinnbank.core.account.infrastructure.configuration;

import com.quinnbank.core.account.domain.policy.AccountOpeningPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AccountDomainConfiguration {

    @Bean
    AccountOpeningPolicy accountOpeningPolicy() {
        return new AccountOpeningPolicy();
    }
}
