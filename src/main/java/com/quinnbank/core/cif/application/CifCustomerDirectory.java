package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.domain.Customer;
import com.quinnbank.core.cif.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class CifCustomerDirectory implements CustomerDirectory {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerSnapshot requireActiveCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> CustomerNotFoundException.byId(customerId));

        if (!customer.isActive()) {
            throw CustomerNotFoundException.activeCustomerRequired(customerId);
        }

        return CustomerSnapshot.from(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerSnapshot> findByCustomerNumber(String customerNumber) {
        if (customerNumber == null || customerNumber.isBlank()) {
            return Optional.empty();
        }

        return customerRepository.findByCustomerNumber(customerNumber.trim())
                .map(CustomerSnapshot::from);
    }
}
