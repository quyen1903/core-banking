package com.quinnbank.core.cif.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;
import com.quinnbank.core.cif.domain.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegisterService implements RegisterCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;
    private final CustomerNumberGeneratorPort customerNumberGenerator;

    public CustomerRegisterService(CustomerRepositoryPort customerRepository, CustomerNumberGeneratorPort customerNumberGenerator) {
        this.customerRepository = customerRepository;
        this.customerNumberGenerator = customerNumberGenerator;
    }
    
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        //check if the customer already exists

        boolean existingCustomer = customerRepository.existsByEmail(command.email());

        if(existingCustomer) {
            throw new IllegalArgumentException("Customer with email " + command.email() + " already exists.");
        }


        String customerNumber = customerNumberGenerator.nextCustomerNumber();

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
