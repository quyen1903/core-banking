package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class IdentityLoginState {
    UUID userId;
    int failedAttempts;
    Instant failureWindowStartedAt;
    Instant lastFailedAt;
    Instant lockedUntil;
    Instant lastAuthenticatedAt;
    Instant createdAt;
    Instant updatedAt;

    public IdentityLoginState(
            UUID userId,
            int failedAttempts,
            Instant failureWindowStartedAt,
            Instant lastFailedAt,
            Instant lockedUntil,
            Instant lastAuthenticatedAt,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.userId = userId;
        this.failedAttempts = failedAttempts;
        this.failureWindowStartedAt = failureWindowStartedAt;
        this.lastFailedAt = lastFailedAt;
        this.lockedUntil = lockedUntil;
        this.lastAuthenticatedAt = lastAuthenticatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static IdentityLoginState create(UUID userId){
        return new IdentityLoginState(
            userId,
            0,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now()
        );
    }
}
