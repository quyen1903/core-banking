package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IdentityLoginState {
    UUID userId;
    int failedAttempts;
    Instant failureWindowStartedAt;
    Instant lastFailedAt;
    Instant lockedUntil;
    Instant lastAuthenticatedAt;
    Instant createdAt;
    Instant updatedAt;

    public static IdentityLoginState restore(
        UUID userId,
        int failedAttempts,
        Instant failureWindowStartedAt,
        Instant lastFailedAt,
        Instant lockedUntil,
        Instant lastAuthenticatedAt,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new IdentityLoginState(userId, failedAttempts, failureWindowStartedAt, lastFailedAt,
            lockedUntil, lastAuthenticatedAt, createdAt, updatedAt);
    }

    public static IdentityLoginState create(UUID userId) {
        Instant now = Instant.now();
        return new IdentityLoginState(
            userId,
            0,
            null,
            null,
            null,
            null,
            now,
            now
        );
    }
}
