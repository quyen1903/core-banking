package com.quinnbank.core.cif.application.service;

import java.time.LocalDateTime;

import com.quinnbank.core.cif.application.port.in.CustomerCommandUseCase;
import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;
import com.quinnbank.core.cif.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerCommandService implements CustomerCommandUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerNumberGeneratorPort customerNumberGeneratorPort;

    public CustomerCommandService(CustomerRepositoryPort customerRepository, CustomerNumberGeneratorPort customerNumberGeneratorPort) {
        this.customerRepository = customerRepository;
        this.customerNumberGeneratorPort = customerNumberGeneratorPort;
    }
    
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        //check if the customer already exists

        boolean existingCustomer = customerRepository.existsByEmail(command.email());

        if(existingCustomer) {
            throw new IllegalArgumentException("Customer with email " + command.email() + " already exists.");
        }


        String customerNumber = customerNumberGeneratorPort.nextCustomerNumber();

        Customer customer = Customer.register(
            customerNumber,
            command.firstName(),
            command.lastName(),
            command.email(),
            command.phone(),
            LocalDateTime.now()
            
        );

        customerRepository.save(customer);
        return new RegisterCustomerResult(customer.getId(), customer.getStatus());
    }
}
