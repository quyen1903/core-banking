package com.quinnbank.core.identity.infrastructure.persistence;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
class IdentityRolePermissionJpaId implements Serializable {

    private Long roleId;
    private Long permissionId;

    IdentityRolePermissionJpaId(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IdentityRolePermissionJpaId that)) {
            return false;
        }

        return Objects.equals(roleId, that.roleId)
                && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, permissionId);
    }
}
