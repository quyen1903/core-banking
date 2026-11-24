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
        Instant failureWindowStartedAt,
        Instant lastFailedAt,
        Instant lockedUntil,
        Instant lastAuthenticatedAt,
        Instant createdAt,
    ) {
        this.userId = userId;
        this.failedAttempts = failedAttempts;
        this.failureWindowStartedAt = failureWindowStartedAt;
        this.lastFailedAt = lastFailedAt;
        this.lockedUntil = lockedUntil;
        this.lastAuthenticatedAt = lastAuthenticatedAt;
        this.updatedAt = updatedAt;
    }
}
