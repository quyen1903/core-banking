package com.quinnbank.core.cif.application.service;

import com.quinnbank.core.cif.application.port.in.CustomerQueryUseCase;
import com.quinnbank.core.cif.application.contract.result.GetCustomerByIdResult;
import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;

import java.util.UUID;

import org.springframework.stereotype.Service;
import com.quinnbank.core.cif.domain.Customer;

@Service
public class CustomerQueryService implements CustomerQueryUseCase {

    private final CustomerRepositoryPort customerRepository;
    
    CustomerQueryService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public GetCustomerByIdResult getCustomerById(UUID customerId) {
        // Implement the logic to retrieve customer by ID

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + customerId + " not found."));
        return new GetCustomerByIdResult(
            customer.getId().toString(), 
            customer.getFirstName(), 
            customer.getLastName(), 
            customer.getEmail(), 
            customer.getPhone()
        );
    }

}
