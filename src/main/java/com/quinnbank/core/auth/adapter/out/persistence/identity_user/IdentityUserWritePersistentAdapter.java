package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import org.hibernate.exception.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;

import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.domain.model.Auth;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;


/**
 * A persistent adapter for writing identity user data to the database.
 */
public class IdentityUserWritePersistentAdapter implements IdentityUserWritePort {
    private final IdentityUserJpaRepository repository;

    public IdentityUserWritePersistentAdapter(IdentityUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUsernameNormalized(String usernameNormalized) {
        return repository.existsByUsernameNormalized(usernameNormalized);
    }

    @Override
    public void save(Auth auth) {
        IdentityUserJpaEntity entity = IdentityUserPersistenceMapper.toNewEntity(auth);
        try {
            repository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException exception) {
            if (isNormalizedUsernameConflict(exception)) {
                throw new DuplicateCustomerEmailException("Customer email is already registered.");
            }
            throw exception;
        }
    }

    private boolean isNormalizedUsernameConflict(Throwable failure) {
        for (Throwable cause = failure; cause != null; cause = cause.getCause()){
            if (cause instanceof ConstraintViolationException violation
                    && "23505".equals(violation.getSQLState())
                    && "uq_identity_user_username".equals(violation.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
