package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.domain.model.IdentityLoginState;

public class IdentityLoginStatePersistenceMapper {

    private IdentityLoginStatePersistenceMapper() {}

    public static IdentityLoginStateJpaEntity toNewEntity(
        IdentityLoginState loginState,
        IdentityUserJpaEntity userRef
    ) {
        IdentityLoginStateJpaEntity entity = new IdentityLoginStateJpaEntity();
        entity.userId = loginState.getUserId();
        entity.user = userRef;
        entity.failedAttempts = loginState.getFailedAttempts();
        entity.failureWindowStartedAt = loginState.getFailureWindowStartedAt();
        entity.lastFailedAt = loginState.getLastFailedAt();
        entity.lockedUntil = loginState.getLockedUntil();
        entity.lastAuthenticatedAt = loginState.getLastAuthenticatedAt();
        entity.createdAt = loginState.getCreatedAt();
        entity.updatedAt = loginState.getUpdatedAt();
        return entity;
    }

    public static IdentityLoginState toNewDomain(IdentityLoginStateJpaEntity entity) {
        return IdentityLoginState.restore(
            entity.userId,
            entity.failedAttempts,
            entity.failureWindowStartedAt,
            entity.lastFailedAt,
            entity.lockedUntil,
            entity.lastAuthenticatedAt,
            entity.createdAt,
            entity.updatedAt
        );
    }
}
