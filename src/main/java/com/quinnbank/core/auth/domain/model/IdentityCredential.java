package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdentityCredential {
    private final UUID userId;
    private final UUID identityPublicId;
    private final String passwordHash;
    private final Instant passwordChangedAt;
    private final boolean mustChangePassword;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static IdentityCredential restore(
        UUID userId,
        UUID identityPublicId,
        String passwordHash, 
        Instant passwordChangedAt, 
        boolean mustChangePassword,
        Instant createdAt,
        Instant updatedAt
    ) {
        return new IdentityCredential(userId, identityPublicId, passwordHash, passwordChangedAt,
            mustChangePassword, createdAt, updatedAt);
    }

    public static IdentityCredential create(
        UUID userId,
        UUID identityPublicId,
        String passwordHash,
        boolean mustChangePassword
    ) {
        Instant now = Instant.now();
        return new IdentityCredential(
            userId,
            identityPublicId,
            passwordHash,
            now,
            mustChangePassword,
            now,
            now
        );
    }
}
