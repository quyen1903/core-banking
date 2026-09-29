package com.quinnbank.core.cif.infrastructure.persistence;

import com.quinnbank.core.cif.application.command.UpdateCustomerCommand;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.application.port.out.CustomerWritePort;
import com.quinnbank.core.cif.domain.model.Customer;

import jakarta.transaction.Transactional;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerWritePersistenceAdapter implements CustomerWritePort {
    private final CustomerJpaRepository repository;

    public CustomerWritePersistenceAdapter(CustomerJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Customer customer) {
        try {
            repository.saveAndFlush(CustomerPersistenceMapper.toNewEntity(customer));
        } catch (DataIntegrityViolationException exception) {
            if (isEmailConflict(exception)) {
                throw new DuplicateCustomerEmailException("Customer email is already registered.");
            }
            throw exception;
        }
    }

    @Override
    public boolean existsByEmail(String normalizedEmail) {
        return repository.existsByEmail(normalizedEmail);
    }

    private static boolean isEmailConflict(Throwable failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation
                    && "23505".equals(violation.getSQLState())
                    && "customers_email_key".equals(violation.getConstraintName())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void updateCustomer(UpdateCustomerCommand command) {
        CustomerJpaEntity entity = repository.
            findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found")); 

        if(command.firstName() != null) entity.firstName = command.firstName();
        if(command.lastName() != null) entity.lastName = command.lastName();
        if(command.phone() != null) entity.phone = command.phone();
        if(command.email() != null) entity.email = command.email();

        entity.updatedAt = java.time.LocalDateTime.now();
        repository.saveAndFlush(entity);
    }
}
