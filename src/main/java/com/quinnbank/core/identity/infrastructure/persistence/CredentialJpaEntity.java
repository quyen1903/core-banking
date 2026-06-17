package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.CredentialType;
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
@Table(name = "credentials")
class CredentialJpaEntity {

    @Id
    private UUID id;

    @Column(name = "identity_account_id", nullable = false)
    private UUID identityAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "credential_type", nullable = false, length = 50)
    private CredentialType credentialType;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    static CredentialJpaEntity create(
            UUID id,
            UUID identityAccountId,
            CredentialType credentialType,
            String passwordHash,
            boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        CredentialJpaEntity entity = new CredentialJpaEntity();
        entity.id = id;
        entity.identityAccountId = identityAccountId;
        entity.credentialType = credentialType;
        entity.passwordHash = passwordHash;
        entity.active = active;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.version = version;
        return entity;
    }
}
