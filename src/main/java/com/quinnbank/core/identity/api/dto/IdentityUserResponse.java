package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record IdentityUserResponse(
        UUID publicId,
        String ownerType,
        Long ownerId,
        String username,
        String email,
        String phoneNumber,
        String status,
        Set<String> roles
) {

    public static IdentityUserResponse from(IdentityUserSnapshot user) {
        return new IdentityUserResponse(
                user.publicId(),
                user.ownerType().name(),
                user.ownerId(),
                user.username(),
                user.email(),
                user.phoneNumber(),
                user.status().name(),
                user.roles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toUnmodifiableSet())
        );
    }
}
