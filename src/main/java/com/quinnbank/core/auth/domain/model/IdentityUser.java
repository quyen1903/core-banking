package com.quinnbank.core.auth.domain.model;

import java.util.Locale;
import java.util.UUID;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

import lombok.Getter;

@Getter 
public final class IdentityUser {
    private UUID id;
    private String username;
    private IdentityOwnerType ownerType;

    private IdentityUser(
        UUID id, 
        String username, 
        IdentityOwnerType ownerType
    ) {
        this.id = id;
        this.username = username;
        this.ownerType = ownerType;
    }

    public static IdentityUser create(
        String username, 
        IdentityOwnerType ownerType
    ) {
        return new IdentityUser(
            UUID.randomUUID(), 
            username, 
            ownerType
        );
    }

    public UUID getId() {
        return id;
    }

    public static String normalizeUsername(String username) {
        return username == null || username.isBlank() ? null : username.trim().toLowerCase(Locale.ROOT);
    }
}
