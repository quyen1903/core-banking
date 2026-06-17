package com.quinnbank.core.identity.application.command;

import com.quinnbank.core.identity.domain.model.IdentityUserStatus;

import java.util.UUID;

public record ChangeIdentityUserStatusCommand(
        UUID publicId,
        IdentityUserStatus status
) {
}
