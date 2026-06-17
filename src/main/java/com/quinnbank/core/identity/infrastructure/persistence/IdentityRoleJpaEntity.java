package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.RoleCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "identity_role")
class IdentityRoleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 80)
    private RoleCode code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    static IdentityRoleJpaEntity create(Long id, RoleCode code, LocalDateTime createdAt) {
        IdentityRoleJpaEntity entity = new IdentityRoleJpaEntity();
        entity.id = id;
        entity.code = code;
        entity.createdAt = createdAt;
        return entity;
    }
}
