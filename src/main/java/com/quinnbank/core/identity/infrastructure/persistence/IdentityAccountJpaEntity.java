package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.IdentityAccountStatus;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "identity_accounts")
class IdentityAccountJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, length = 50)
    private IdentitySubjectType subjectType;

    @Column(name = "subject_id")
    private UUID subjectId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IdentityAccountStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    static IdentityAccountJpaEntity create(
            UUID id,
            String username,
            String email,
            IdentitySubjectType subjectType,
            UUID subjectId,
            IdentityAccountStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        IdentityAccountJpaEntity entity = new IdentityAccountJpaEntity();
        entity.id = id;
        entity.username = username;
        entity.email = email;
        entity.subjectType = subjectType;
        entity.subjectId = subjectId;
        entity.status = status;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.version = version;
        return entity;
    }
}
