package com.quinnbank.core.identity.infrastructure.persistence;

import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
class IdentityUserRoleJpaId implements Serializable {

    private Long identityUserId;
    private Long roleId;

    IdentityUserRoleJpaId(Long identityUserId, Long roleId) {
        this.identityUserId = identityUserId;
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IdentityUserRoleJpaId that)) {
            return false;
        }

        return Objects.equals(identityUserId, that.identityUserId)
                && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityUserId, roleId);
    }
}
