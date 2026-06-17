package com.quinnbank.core.identity.domain.model;

import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;

import java.time.LocalDateTime;
import java.util.UUID;

public final class Credential {

    private final UUID id;
    private final UUID identityAccountId;
    private final CredentialType type;
    private final String passwordHash;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;

    private Credential(
            UUID id,
            UUID identityAccountId,
            CredentialType type,
            String passwordHash,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        this.id = id;
        this.identityAccountId = identityAccountId;
        this.type = type;
        this.passwordHash = passwordHash;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Credential password(UUID identityAccountId, String passwordHash, LocalDateTime createdAt) {
        if (identityAccountId == null) {
            throw new IdentityAccountCreationRejectedException("identity account id is required for credential");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IdentityAccountCreationRejectedException("password hash is required");
        }
        if (createdAt == null) {
            throw new IdentityAccountCreationRejectedException("credential creation time is required");
        }

        return new Credential(
                UUID.randomUUID(),
                identityAccountId,
                CredentialType.PASSWORD,
                passwordHash,
                true,
                createdAt,
                createdAt,
                0
        );
    }

    public static Credential restore(
            UUID id,
            UUID identityAccountId,
            CredentialType type,
            String passwordHash,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        if (id == null) {
            throw new IllegalArgumentException("credential id is required");
        }
        if (identityAccountId == null) {
            throw new IllegalArgumentException("identity account id is required for credential");
        }
        if (type == null) {
            throw new IllegalArgumentException("credential type is required");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("password hash is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("credential timestamps are required");
        }

        return new Credential(id, identityAccountId, type, passwordHash, active, createdAt, updatedAt, version);
    }

    public UUID id() {
        return id;
    }

    public UUID identityAccountId() {
        return identityAccountId;
    }

    public CredentialType type() {
        return type;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public boolean active() {
        return active;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }
}
