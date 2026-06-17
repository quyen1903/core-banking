package com.quinnbank.core.identity.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(IdentityRolePermissionJpaId.class)
@Table(name = "identity_role_permission")
class IdentityRolePermissionJpaEntity {

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Id
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    static IdentityRolePermissionJpaEntity create(Long roleId, Long permissionId, LocalDateTime createdAt) {
        IdentityRolePermissionJpaEntity entity = new IdentityRolePermissionJpaEntity();
        entity.roleId = roleId;
        entity.permissionId = permissionId;
        entity.createdAt = createdAt;
        return entity;
    }
}
