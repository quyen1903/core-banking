package com.quinnbank.core.identity.application.result;

import com.quinnbank.core.identity.domain.model.IdentityOwnerType;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.IdentityUserStatus;
import com.quinnbank.core.identity.domain.model.RoleCode;

import java.util.Set;
import java.util.UUID;

public record IdentityUserSnapshot(
        UUID publicId,
        IdentityOwnerType ownerType,
        Long ownerId,
        String username,
        String email,
        String phoneNumber,
        IdentityUserStatus status,
        Set<RoleCode> roles
) {

    public static IdentityUserSnapshot from(IdentityUser user, Set<RoleCode> roles) {
        return new IdentityUserSnapshot(
                user.publicId(),
                user.ownerType(),
                user.ownerId(),
                user.username(),
                user.email(),
                user.phoneNumber(),
                user.status(),
                Set.copyOf(roles)
        );
    }
}
