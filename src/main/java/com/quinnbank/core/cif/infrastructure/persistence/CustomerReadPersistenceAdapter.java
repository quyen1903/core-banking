package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerReadPersistenceAdapter implements CustomerReadPort {
    private final CustomerJpaRepository repository;

    public CustomerReadPersistenceAdapter(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<GetCustomerByIdResult> findById(UUID customerId) {
        return repository.findDetailsById(customerId);
    }
}
