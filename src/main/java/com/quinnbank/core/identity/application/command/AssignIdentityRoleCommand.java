package com.quinnbank.core.identity.application.command;

import com.quinnbank.core.identity.domain.model.RoleCode;

import java.util.UUID;

public record AssignIdentityRoleCommand(
        UUID publicId,
        RoleCode roleCode
) {
}
