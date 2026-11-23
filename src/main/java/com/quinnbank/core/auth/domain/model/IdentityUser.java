package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

import lombok.Getter;

@Getter 
public final class IdentityUser {
    private final UUID id;
    private final UUID publicId;
    private final String username;
    private final IdentityOwnerType ownerType;
    private final UUID ownerId;
    private final IdentityStatus status;
    private final long securityVersion;
    private final Instant createAt;
    private final Instant updateAt;
    private final Long version;

    public IdentityUser(
        UUID id,
        UUID publicId,
        String username, 
        IdentityOwnerType ownerType,
        UUID ownerId,
        IdentityStatus status,
        long securityVersion,
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
        this.createAt = Instant.now();
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
