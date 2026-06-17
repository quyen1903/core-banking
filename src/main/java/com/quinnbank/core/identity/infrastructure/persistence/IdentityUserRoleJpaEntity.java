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
@IdClass(IdentityUserRoleJpaId.class)
@Table(name = "identity_user_role")
class IdentityUserRoleJpaEntity {

    @Id
    @Column(name = "identity_user_id", nullable = false)
    private Long identityUserId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    static IdentityUserRoleJpaEntity create(Long identityUserId, Long roleId, LocalDateTime assignedAt) {
        IdentityUserRoleJpaEntity entity = new IdentityUserRoleJpaEntity();
        entity.identityUserId = identityUserId;
        entity.roleId = roleId;
        entity.assignedAt = assignedAt;
        return entity;
    }
}
