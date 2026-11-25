package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaRepository;
import com.quinnbank.core.auth.application.port.out.IdentityLoginStateWritePort;
import com.quinnbank.core.auth.domain.model.IdentityLoginState;

import java.util.Optional;
import java.util.UUID;

public class IdentityLoginStateWritePersistenceAdapter implements IdentityLoginStateWritePort {
    private final IdentityLoginStateJpaRepository repository;
    private final IdentityUserJpaRepository userRepository;

    public IdentityLoginStateWritePersistenceAdapter (
            IdentityLoginStateJpaRepository repository,
            IdentityUserJpaRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void save(IdentityLoginState loginState) {
        IdentityUserJpaEntity userRef = userRepository.getReferenceById(loginState.getUserId());
        IdentityLoginStateJpaEntity entity = IdentityLoginStatePersistenceMapper.toNewEntity(loginState, userRef);
        try {
            repository.saveAndFlush(entity);
        } catch (Exception exception) {
            throw new RuntimeException("Error occurred while saving identity login state", exception);
        }
    }

    @Override
    public Optional<IdentityLoginState> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(IdentityLoginStatePersistenceMapper::toNewDomain);
    }

}
