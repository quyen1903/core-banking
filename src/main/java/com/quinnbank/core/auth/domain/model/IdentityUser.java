package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

import lombok.Getter;

@Getter 
public final class IdentityUser {
    private UUID id;
    private UUID publicId;
    private String username;
    private IdentityOwnerType ownerType;
    private UUID ownerId;
    private IdentityStatus status;
    private long securityVersion;
    private Instant createAt;
    private Instant updateAt;
    private Long version;

    private IdentityUser(
        UUID id,
        UUID publicId,
        String username, 
        IdentityOwnerType ownerType,
        UUID ownerId,
        IdentityStatus status,
        long securityVersion,
        Instant createAt,
        Instant updateAt,
        Long version
    ) {
        this.id = id;
        this.publicId = publicId;
        this.username = username;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.status = status;
        this.securityVersion = securityVersion;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.version = version;
    }

    public static IdentityUser create(
        String username, 
        IdentityOwnerType ownerType,
        UUID ownerId
    ) {

        return new IdentityUser(
            UUID.randomUUID(),
            UUID.randomUUID(),
            username, 
            ownerType,
            ownerId,
            IdentityStatus.PENDING_ACTIVATION,
            1L,
            Instant.now(),
            Instant.now(),
            null
        );
    }

    public UUID getId() {
        return id;
    }

    public static String normalizeUsername(String username) {
        return username == null || username.isBlank() ? null : username.trim().toLowerCase(Locale.ROOT);
    }
}
