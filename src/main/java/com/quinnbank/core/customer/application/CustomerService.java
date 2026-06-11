package com.quinnbank.core.customer.application;

import com.quinnbank.core.customer.domain.CustomerRepository;
import com.quinnbank.core.customer.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @PostMapping
    public Customer createCustomer(CreateCustomerCommand command){
        String email = normalizeEmail(command.email());

        if(email != null && customerRepository.existsByEmail(email)){
            throw new IllegalArgumentException("email already exists");
        }

        Customer customer = Customer.create(
                command.fullName(),
                email,
                command.phone()
        );
        return customerRepository.save(customer);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

}
