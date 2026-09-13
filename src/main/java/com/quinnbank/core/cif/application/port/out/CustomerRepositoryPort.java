package com.quinnbank.core.cif.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.quinnbank.core.cif.domain.Customer;

public interface CustomerRepositoryPort {

    public Customer save(Customer customer);

    public Optional<Customer> findById(UUID customerId);

    public boolean existsByEmail(String email);

    public Optional<Customer> findByCustomerNumber(String customerNumber);

    public Customer registerCustomer(Customer customer);
}
