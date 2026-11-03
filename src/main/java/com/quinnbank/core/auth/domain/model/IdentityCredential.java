package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public final class  IdentityCredential {
    private UUID userId;
    private UUID identityPublicId;
    private String passwordHash;
    private Instant passwordChangedAt;
    private boolean mustChangePassword;
    private Instant createdAt;
    private Instant updatedAt;

    private IdentityCredential(
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
