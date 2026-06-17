package com.quinnbank.core.identity.domain.model;

import java.time.LocalDateTime;

public final class UserRole {

    private final Long identityUserId;
    private final Long roleId;
    private final RoleCode roleCode;
    private final LocalDateTime assignedAt;

    private UserRole(Long identityUserId, Long roleId, RoleCode roleCode, LocalDateTime assignedAt) {
        this.identityUserId = identityUserId;
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.assignedAt = assignedAt;
    }

    public static UserRole assign(Long identityUserId, Role role, LocalDateTime assignedAt) {
        if (identityUserId == null || identityUserId <= 0) {
            throw new IllegalArgumentException("identity user id is required for role assignment");
        }
        if (role == null) {
            throw new IllegalArgumentException("role is required for role assignment");
        }
        if (assignedAt == null) {
            throw new IllegalArgumentException("role assignment time is required");
        }

        return new UserRole(identityUserId, role.id(), role.code(), assignedAt);
    }

    public static UserRole restore(Long identityUserId, Long roleId, RoleCode roleCode, LocalDateTime assignedAt) {
        if (identityUserId == null || roleId == null || roleCode == null || assignedAt == null) {
            throw new IllegalArgumentException("user role fields are required");
        }

        return new UserRole(identityUserId, roleId, roleCode, assignedAt);
    }

    public Long identityUserId() {
        return identityUserId;
    }

    public Long roleId() {
        return roleId;
    }

    public RoleCode roleCode() {
        return roleCode;
    }

    public LocalDateTime assignedAt() {
        return assignedAt;
    }
}
