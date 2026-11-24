package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public final class IdentityCredential {
    private final UUID userId;
    private final UUID identityPublicId;
    private final String passwordHash;
    private final Instant passwordChangedAt;
    private final boolean mustChangePassword;
    private final Instant createdAt;
    private final Instant updatedAt;

    public IdentityCredential(
        UUID userId,
        String passwordHash, 
        Instant passwordChangedAt, 
        boolean mustChangePassword
    ) {
        this.userId = userId;
        this.identityPublicId = userId;
        this.passwordHash = passwordHash;
        this.passwordChangedAt = passwordChangedAt;
        this.mustChangePassword = mustChangePassword;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public static IdentityCredential create(
        UUID userId,
        String passwordHash,
        boolean mustChangePassword
    ) {
        return new IdentityCredential(
            userId,
            passwordHash,
            Instant.now(),
            mustChangePassword
        );
    }
}
