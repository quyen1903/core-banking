package com.quinnbank.core.cif.application.port.out;

import com.quinnbank.core.cif.domain.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID customerId);

    boolean existsByEmail(String email);

    Optional<Customer> findByCustomerNumber(String customerNumber);
}
