package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import org.springframework.dao.DataIntegrityViolationException;

import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.domain.model.Auth;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.domain.model.Customer;

public class IdentityUserWritePersistentAdapter implements IdentityUserWritePort {
    private final IdentityUserJpaRepository repository;

    public IdentityUserWritePersistentAdapter(IdentityUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUsernameNormalized(String usernameNormalized) {
        return repository.existsByUsernameNormalized(usernameNormalized);
    }

    public void save(Auth auth) {
        try {
            repository.saveAndFlush(auth);
        } catch (DataIntegrityViolationException exception) {
            if (isEmailConflict(exception)) {
                throw new DuplicateCustomerEmailException("Customer email is already registered.");
            }
            throw exception;
        }
    }
}
