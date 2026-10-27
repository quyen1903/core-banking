package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;

@Getter
public final class  IdentityCredential {
    private UUID identityPublicId;
    private String passwordHash;
    private Instant passwordChangedAt;
    private boolean mustChangePassword;

    private IdentityCredential(
        UUID identityPublicId, 
        String passwordHash, 
        Instant passwordChangedAt, 
        boolean mustChangePassword
    ) {
        this.identityPublicId = identityPublicId;
        this.passwordHash = passwordHash;
        this.passwordChangedAt = passwordChangedAt;
        this.mustChangePassword = mustChangePassword;
    }

    public static IdentityCredential create(
        UUID identityPublicId, 
        String passwordHash, 
        Instant passwordChangedAt, 
        boolean mustChangePassword
    ) {
        return new IdentityCredential(
            identityPublicId, 
            passwordHash, 
            passwordChangedAt, 
            mustChangePassword
        );
    }
}
