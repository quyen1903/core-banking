package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import org.springframework.stereotype.Repository;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaRepository;
import com.quinnbank.core.auth.application.port.out.IdentityCredentialWritePort;
import com.quinnbank.core.auth.domain.model.IdentityCredential;

@Repository 
public class IdentityCredentialWritePersistenceAdapter implements IdentityCredentialWritePort {
    private final IdentityCredentialJpaRepository repository;
    private final IdentityUserJpaRepository userRepository;

    public IdentityCredentialWritePersistenceAdapter(
        IdentityCredentialJpaRepository repository,
        IdentityUserJpaRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(IdentityCredential identityCredential) {
        IdentityUserJpaEntity userRef = userRepository.getReferenceById(identityCredential.getUserId());
        IdentityCredentialJpaEntity entity = IdentityCredentialPersistenceMapper.toNewEntity(identityCredential, userRef);
        try{
            repository.saveAndFlush(entity);
        }catch(Exception exception){
            throw new RuntimeException("Error occurred while saving identity credential", exception);
        }
    }
}
