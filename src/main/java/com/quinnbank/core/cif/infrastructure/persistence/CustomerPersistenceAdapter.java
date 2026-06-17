package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import com.quinnbank.core.cif.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final SpringDataCustomerRepository repository;

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        return repository.findById(customerId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<Customer> findByCustomerNumber(String customerNumber) {
        return repository.findByCustomerNumber(customerNumber);
    }
}
