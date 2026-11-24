package com.quinnbank.core.auth.domain.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class IdentityLoginState {

    UUID userId;
    int failedAttempts;
    Instant failureWindowStartedAt;
    Instant lastFailedAt;
    Instant lockedUntil;
    Instant lastAuthenticatedAt;
    Instant createdAt;
    Instant updatedAt;
    Long version;

    public IdentityLoginState(
        UUID userId, int failedAttempts,
        Instant failureWindowStartedAt,
        Instant lastFailedAt,
        Instant lockedUntil,
        Instant lastAuthenticatedAt,
        Instant createdAt,
        Instant updatedAt,
        Long version
    ) {
        this.userId = userId;
        this.failedAttempts = failedAttempts;
        this.failureWindowStartedAt = failureWindowStartedAt;
        this.lastFailedAt = lastFailedAt;
        this.lockedUntil = lockedUntil;
        this.lastAuthenticatedAt = lastAuthenticatedAt;
        this.createdAt = Instant.now();
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
