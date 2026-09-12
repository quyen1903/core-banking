package com.quinnbank.core.cif.adapter.out.persistent;

import java.util.Optional;
import java.util.UUID;

import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import com.quinnbank.core.cif.domain.Customer;

public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerPersistenceAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        return customerJpaRepository.save(customer);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return customerJpaRepository.findById(customerId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Customer> findByCustomerNumber(String customerNumber) {
        return customerJpaRepository.findByCustomerNumber(customerNumber);
    }
}
