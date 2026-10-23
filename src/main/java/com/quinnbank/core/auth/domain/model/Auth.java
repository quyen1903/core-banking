package com.quinnbank.core.auth.domain.model;

import java.util.Locale;
import java.util.UUID;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;

import lombok.Getter;

@Getter 
public final class Auth {
    private UUID id;
    private String username;
    private IdentityOwnerType ownerType;

    private Auth(
        UUID id, 
        String username, 
        IdentityOwnerType ownerType
    ) {
        this.id = id;
        this.username = username;
        this.ownerType = ownerType;
    }

    public static Auth create(
        String username, 
        IdentityOwnerType ownerType
    ) {
        return new Auth(
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
