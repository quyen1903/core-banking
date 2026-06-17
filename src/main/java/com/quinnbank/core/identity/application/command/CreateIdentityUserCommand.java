package com.quinnbank.core.identity.application.command;

import com.quinnbank.core.identity.domain.model.IdentityOwnerType;

public record CreateIdentityUserCommand(
        IdentityOwnerType ownerType,
        Long ownerId,
        String username,
        String email,
        String phoneNumber
) {
}
