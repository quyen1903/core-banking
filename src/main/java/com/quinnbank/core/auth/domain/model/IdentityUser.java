package com.quinnbank.core.auth.domain.model;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static IdentityUser restore(
        UUID id,
        UUID publicId,
        String username, 
        IdentityOwnerType ownerType,
        UUID ownerId,
        IdentityStatus status,
        long securityVersion,
        Instant createdAt,
        Instant updatedAt,
        Long version
    ) {
        return new IdentityUser(id, publicId, username, ownerType, ownerId, status,
            securityVersion, createdAt, updatedAt, version);
    }

    public static IdentityUser create(
        String username, 
        IdentityOwnerType ownerType,
        UUID ownerId
    ) {
        Instant now = Instant.now();
        return new IdentityUser(
            UUID.randomUUID(),
            UUID.randomUUID(),
            username, 
            ownerType,
            ownerId,
            IdentityStatus.PENDING_ACTIVATION,
            1L,
            now,
            now,
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
