package com.quinnbank.core.auth.adapter.out.configuration;

import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;

import org.springframework.transaction.annotation.Transactional;

public class TransactionalCommandAuthUseCase implements CommandAuthUseCase{
    private final CommandAuthUseCase delegate;
    
    TransactionalCommandAuthUseCase(CommandAuthUseCase delegate){
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public RegisterIdentityUserResult register(RegisterIdentityUserCommand command){
        return delegate.register(command);
    }
}
