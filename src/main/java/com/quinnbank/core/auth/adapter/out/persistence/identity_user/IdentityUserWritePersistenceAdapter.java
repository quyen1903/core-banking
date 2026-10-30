package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import org.hibernate.exception.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.domain.model.IdentityUser;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;


/**
 * A persistent adapter for writing identity user data to the database.
 */
@Repository 
public class IdentityUserWritePersistenceAdapter implements IdentityUserWritePort {
    private final IdentityUserJpaRepository repository;

    public IdentityUserWritePersistenceAdapter(IdentityUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByUsernameNormalized(String usernameNormalized) {
        return repository.existsByUsernameNormalized(usernameNormalized);
    }

    @Override
    public void save(IdentityUser identityUser) {
        IdentityUserJpaEntity entity = IdentityUserPersistenceMapper.toNewEntity(identityUser);
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
