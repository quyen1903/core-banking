package com.quinnbank.core.identity.domain.event;

import com.quinnbank.core.identity.domain.model.IdentityOwnerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record IdentityUserCreatedEvent(
        UUID publicId,
        IdentityOwnerType ownerType,
        Long ownerId,
        LocalDateTime createdAt
) {
}
