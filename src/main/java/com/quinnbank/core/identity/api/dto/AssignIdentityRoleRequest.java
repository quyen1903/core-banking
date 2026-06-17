package com.quinnbank.core.identity.api.dto;

import com.quinnbank.core.identity.domain.model.RoleCode;
import jakarta.validation.constraints.NotNull;

public record AssignIdentityRoleRequest(
        @NotNull
        RoleCode roleCode
) {
}
