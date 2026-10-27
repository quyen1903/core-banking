package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import com.quinnbank.core.auth.application.port.out.IdentityCredentialWritePort;
import com.quinnbank.core.auth.domain.model.IdentityCredential;

public class IdentityCredentialWritePersistenceAdapter implements IdentityCredentialWritePort {
    private final IdentityCredentialJpaRepository repository;

    public IdentityCredentialWritePersistenceAdapter(IdentityCredentialJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(IdentityCredential identityCredential) {
        IdentityCredentialJpaEntity entity = IdentityCredentialPersistenceMapper.toNewEntity(identityCredential);
        try{
            repository.saveAndFlush(entity);
        }catch(Exception exception){
            throw new RuntimeException("Error occurred while saving identity credential", exception);
        }
    }

}
