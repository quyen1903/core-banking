package com.quinnbank.core.auth.adapter.out.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;
import com.quinnbank.core.auth.application.port.out.IdentityCredentialWritePort;
import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.application.service.AuthCommandService;

@Configuration(proxyBeanMethods = false)
public class AuthUseCaseConfiguration {
    @Bean
    CommandAuthUseCase commandAuthUseCase(
        IdentityUserWritePort identityUser,
        IdentityCredentialWritePort identityCredential,
        PasswordHashingPort passwordHashing

    ){
        return new TransactionalCommandAuthUseCase(new AuthCommandService(identityUser, identityCredential, passwordHashing));
    };
}
