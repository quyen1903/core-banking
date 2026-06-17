package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.PermissionCode;
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
@Table(name = "identity_permission")
class IdentityPermissionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 120)
    private PermissionCode code;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    static IdentityPermissionJpaEntity create(Long id, PermissionCode code, LocalDateTime createdAt) {
        IdentityPermissionJpaEntity entity = new IdentityPermissionJpaEntity();
        entity.id = id;
        entity.code = code;
        entity.createdAt = createdAt;
        return entity;
    }
}
