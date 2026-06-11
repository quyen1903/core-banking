package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCustomerProfileQuery {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerSnapshot findById(UUID customerId) {
        return customerRepository.findById(customerId)
                .map(CustomerSnapshot::from)
                .orElseThrow(() -> CustomerNotFoundException.byId(customerId));
    }
}
