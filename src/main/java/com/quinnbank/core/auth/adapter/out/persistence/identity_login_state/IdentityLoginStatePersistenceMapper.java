package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.domain.model.IdentityLoginState;

public class IdentityLoginStatePersistenceMapper {
    public IdentityLoginStatePersistenceMapper() {}

    public static IdentityLoginStateJpaEntity toNewEntity(
        IdentityLoginState identityLoginState,
        IdentityUserJpaEntity userReference
    ) {
        IdentityLoginStateJpaEntity entity = new IdentityLoginStateJpaEntity();
        entity.userId = identityLoginState.getUserId();
        entity.user = userReference;
        entity.failedAttempts = identityLoginState.getFailedAttempts();
        entity.failureWindowStartedAt = identityLoginState.getFailureWindowStartedAt();
        entity.lastFailedAt =  identityLoginState.getLastFailedAt();
        entity.lockedUntil = identityLoginState.getLockedUntil();
        entity.lastAuthenticatedAt = identityLoginState.getLastAuthenticatedAt();
        entity.createdAt = identityLoginState.getCreatedAt();
        entity.updatedAt = identityLoginState.getUpdatedAt();
        entity.version = identityLoginState.getVersion();

        return entity;
    }

    public static IdentityLoginState toNewDomain(
        IdentityLoginStateJpaEntity entity
    ) {

    }
}
