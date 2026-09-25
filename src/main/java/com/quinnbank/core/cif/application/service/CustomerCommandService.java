package com.quinnbank.core.cif.application.service;

import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;
import com.quinnbank.core.cif.application.port.out.CustomerWritePort;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;
import com.quinnbank.core.cif.domain.model.Customer;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

public final class CustomerCommandService implements RegisterCustomerUseCase {
    private final CustomerWritePort customers;
    private final CustomerNumberGeneratorPort customerNumbers;
    private final Clock clock;

    public CustomerCommandService(
        CustomerWritePort customers,
        CustomerNumberGeneratorPort customerNumbers, 
        Clock clock
    ) {
        this.customers = Objects.requireNonNull(customers);
        this.customerNumbers = Objects.requireNonNull(customerNumbers);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        if (command == null) {
            throw new CustomerRegistrationRejectedException("registration command is required");
        }
        String email = Customer.normalizeEmail(command.email());
        if (email != null && customers.existsByEmail(email)) {
            throw new DuplicateCustomerEmailException("Customer email is already registered.");
        }
        Customer customer = Customer.register(
                customerNumbers.nextCustomerNumber(), 
                command.firstName(), 
                command.lastName(),
                email, 
                command.phone(), 
                command.officeId(), 
                command.externalId(), 
                LocalDateTime.now(clock)
            );
        customers.save(customer);
        return new RegisterCustomerResult(customer.getId(), customer.getStatus().name());
    }
}
