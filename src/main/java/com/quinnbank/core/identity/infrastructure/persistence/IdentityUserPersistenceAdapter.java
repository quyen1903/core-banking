package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class IdentityUserPersistenceAdapter implements IdentityUserRepositoryPort {

    private final SpringDataIdentityUserRepository repository;
    private final IdentityPersistenceMapper mapper = new IdentityPersistenceMapper();

    @Override
    public IdentityUser save(IdentityUser identityUser) {
        return mapper.toDomain(repository.save(mapper.toIdentityUserEntity(identityUser)));
    }

    @Override
    public Optional<IdentityUser> findByPublicId(UUID publicId) {
        return repository.findByPublicId(publicId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }
}
