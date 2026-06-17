package com.quinnbank.core.identity.domain.model;

import com.quinnbank.core.identity.domain.exception.IdentityCredentialRejectedException;

import java.time.LocalDateTime;

public final class IdentityCredential {

    private final Long id;
    private final Long identityUserId;
    private final IdentityCredentialType type;
    private final String passwordHash;
    private final LocalDateTime passwordChangedOnUtc;
    private final boolean mustChangePassword;
    private final LocalDateTime createdOnUtc;

    private IdentityCredential(
            Long id,
            Long identityUserId,
            IdentityCredentialType type,
            String passwordHash,
            LocalDateTime passwordChangedOnUtc,
            boolean mustChangePassword,
            LocalDateTime createdOnUtc
    ) {
        this.id = id;
        this.identityUserId = identityUserId;
        this.type = type;
        this.passwordHash = passwordHash;
        this.passwordChangedOnUtc = passwordChangedOnUtc;
        this.mustChangePassword = mustChangePassword;
        this.createdOnUtc = createdOnUtc;
    }

    public static IdentityCredential password(
            Long identityUserId,
            String passwordHash,
            boolean mustChangePassword,
            LocalDateTime changedOnUtc
    ) {
        requireIdentityUserId(identityUserId);
        requirePasswordHash(passwordHash);
        if (changedOnUtc == null) {
            throw new IdentityCredentialRejectedException("password change time is required");
        }

        return new IdentityCredential(
                null,
                identityUserId,
                IdentityCredentialType.PASSWORD,
                passwordHash,
                changedOnUtc,
                mustChangePassword,
                changedOnUtc
        );
    }

    public static IdentityCredential restore(
            Long id,
            Long identityUserId,
            IdentityCredentialType type,
            String passwordHash,
            LocalDateTime passwordChangedOnUtc,
            boolean mustChangePassword,
            LocalDateTime createdOnUtc
    ) {
        if (id == null) {
            throw new IllegalArgumentException("identity credential id is required");
        }
        requireIdentityUserId(identityUserId);
        if (type == null) {
            throw new IllegalArgumentException("identity credential type is required");
        }
        requirePasswordHash(passwordHash);
        if (passwordChangedOnUtc == null || createdOnUtc == null) {
            throw new IllegalArgumentException("identity credential timestamps are required");
        }

        return new IdentityCredential(
                id,
                identityUserId,
                type,
                passwordHash,
                passwordChangedOnUtc,
                mustChangePassword,
                createdOnUtc
        );
    }

    public IdentityCredential replacePassword(
            String newPasswordHash,
            boolean newMustChangePassword,
            LocalDateTime changedOnUtc
    ) {
        requirePasswordHash(newPasswordHash);
        if (changedOnUtc == null) {
            throw new IdentityCredentialRejectedException("password change time is required");
        }

        return new IdentityCredential(
                id,
                identityUserId,
                type,
                newPasswordHash,
                changedOnUtc,
                newMustChangePassword,
                createdOnUtc
        );
    }

    public Long id() {
        return id;
    }

    public Long identityUserId() {
        return identityUserId;
    }

    public IdentityCredentialType type() {
        return type;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public LocalDateTime passwordChangedOnUtc() {
        return passwordChangedOnUtc;
    }

    public boolean mustChangePassword() {
        return mustChangePassword;
    }

    public LocalDateTime createdOnUtc() {
        return createdOnUtc;
    }

    private static void requireIdentityUserId(Long identityUserId) {
        if (identityUserId == null || identityUserId <= 0) {
            throw new IdentityCredentialRejectedException("identity user id is required for credential");
        }
    }

    private static void requirePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IdentityCredentialRejectedException("password hash is required");
        }
    }
}
