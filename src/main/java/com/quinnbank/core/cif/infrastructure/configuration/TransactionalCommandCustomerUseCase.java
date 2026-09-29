package com.quinnbank.core.cif.infrastructure.configuration;

import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.command.UpdateCustomerCommand;
import com.quinnbank.core.cif.application.port.in.CommandCustomerUseCase;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.result.UpdateCustomerResult;
import org.springframework.transaction.annotation.Transactional;

/** Decorates the framework-independent handler with the registration transaction boundary. */
public class TransactionalCommandCustomerUseCase implements CommandCustomerUseCase {
    private final CommandCustomerUseCase delegate;

    public TransactionalCommandCustomerUseCase(CommandCustomerUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        return delegate.registerCustomer(command);
    }

    @Override
    @Transactional
    public UpdateCustomerResult updateCustomer(UpdateCustomerCommand command) {
        return delegate.updateCustomer(command);
    }
}
