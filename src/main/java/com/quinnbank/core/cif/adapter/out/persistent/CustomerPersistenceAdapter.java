package com.quinnbank.core.cif.adapter.out.persistent;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.quinnbank.core.cif.application.port.out.CustomerRepositoryPort;
import com.quinnbank.core.cif.domain.Customer;

public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;
    private final Customer customer;

    public CustomerPersistenceAdapter(
        CustomerJpaRepository customerJpaRepository,
        Customer customer
    ) {
        this.customerJpaRepository = customerJpaRepository;
        this.customer = customer;
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

    @Override
    public Customer registerCustomer(Customer customer) {
        return Customer.register(
            customer.getCustomerNumber(), 
            customer.getFirstName(), 
            customer.getLastName(), 
            customer.getEmail(), 
            customer.getPhone(),
            LocalDateTime.now()
        );
    }
}
