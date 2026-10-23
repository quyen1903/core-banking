package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public final class  PasswordCredential {
    private UUID identityPublicId;
    private String passwordHash;
    private Date passwordChangedAt;
    private boolean mustChangePassword;

    private PasswordCredential(
        UUID identityPublicId, 
        String passwordHash, 
        Date passwordChangedAt, 
        boolean mustChangePassword
    ) {
        this.identityPublicId = identityPublicId;
        this.passwordHash = passwordHash;
        this.passwordChangedAt = passwordChangedAt;
        this.mustChangePassword = mustChangePassword;
    }

    public static PasswordCredential create(
        UUID identityPublicId, 
        String passwordHash, 
        Instant passwordChangedAt, 
        boolean mustChangePassword
    ) {
        return new PasswordCredential(identityPublicId, passwordHash, Date.from(passwordChangedAt), mustChangePassword);
    }
}
