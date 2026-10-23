package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;

public class IdentityUserWritePersistentAdapter implements IdentityUserWritePort {
    private final IdentityUserJpaRepository identityUserJpaRepository;

    public IdentityUserWritePersistentAdapter(IdentityUserJpaRepository identityUserJpaRepository) {
        this.identityUserJpaRepository = identityUserJpaRepository;
    }

    @Override
    public boolean existsByUsernameNormalized(String usernameNormalized) {
        return identityUserJpaRepository.existsByUsernameNormalized(usernameNormalized);
    }
}
