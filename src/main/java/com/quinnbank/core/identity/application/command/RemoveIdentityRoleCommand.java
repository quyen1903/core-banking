package com.quinnbank.core.identity.application.command;

import com.quinnbank.core.identity.domain.model.RoleCode;

import java.util.UUID;

public record RemoveIdentityRoleCommand(
        UUID publicId,
        RoleCode roleCode
) {
}
