package com.quinnbank.core.identity.domain.model;

import java.time.LocalDateTime;

public record RolePermission(
        RoleCode roleCode,
        PermissionCode permissionCode,
        LocalDateTime assignedAt
) {

    public RolePermission {
        if (roleCode == null) {
            throw new IllegalArgumentException("role code is required");
        }
        if (permissionCode == null) {
            throw new IllegalArgumentException("permission code is required");
        }
        if (assignedAt == null) {
            throw new IllegalArgumentException("role permission assignment time is required");
        }
    }
}
