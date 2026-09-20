package com.quinnbank.core.cif.infrastructure.configuration;

import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import org.springframework.transaction.annotation.Transactional;

/** Decorates the framework-independent handler with the registration transaction boundary. */
public class TransactionalRegisterCustomerUseCase implements RegisterCustomerUseCase {
    private final RegisterCustomerUseCase delegate;

    public TransactionalRegisterCustomerUseCase(RegisterCustomerUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        return delegate.registerCustomer(command);
    }
}
