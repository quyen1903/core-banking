package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.application.port.out.IdentityCredentialRepositoryPort;
import com.quinnbank.core.identity.domain.model.IdentityCredential;
import com.quinnbank.core.identity.domain.model.IdentityCredentialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class IdentityCredentialPersistenceAdapter implements IdentityCredentialRepositoryPort {

    private final SpringDataIdentityCredentialRepository repository;
    private final IdentityPersistenceMapper mapper = new IdentityPersistenceMapper();

    @Override
    public IdentityCredential save(IdentityCredential credential) {
        return mapper.toDomain(repository.save(mapper.toIdentityCredentialEntity(credential)));
    }

    @Override
    public Optional<IdentityCredential> findPasswordCredentialByIdentityUserId(Long identityUserId) {
        return repository.findByIdentityUserIdAndType(identityUserId, IdentityCredentialType.PASSWORD)
                .map(mapper::toDomain);
    }
}
