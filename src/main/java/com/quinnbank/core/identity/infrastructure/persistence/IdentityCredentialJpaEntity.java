package com.quinnbank.core.identity.infrastructure.persistence;

import com.quinnbank.core.identity.domain.model.IdentityCredentialType;
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
@Table(name = "identity_credential")
class IdentityCredentialJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identity_user_id", nullable = false)
    private Long identityUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IdentityCredentialType type;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_changed_on_utc", nullable = false)
    private LocalDateTime passwordChangedOnUtc;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "created_on_utc", nullable = false)
    private LocalDateTime createdOnUtc;

    static IdentityCredentialJpaEntity create(
            Long id,
            Long identityUserId,
            IdentityCredentialType type,
            String passwordHash,
            LocalDateTime passwordChangedOnUtc,
            boolean mustChangePassword,
            LocalDateTime createdOnUtc
    ) {
        IdentityCredentialJpaEntity entity = new IdentityCredentialJpaEntity();
        entity.id = id;
        entity.identityUserId = identityUserId;
        entity.type = type;
        entity.passwordHash = passwordHash;
        entity.passwordChangedOnUtc = passwordChangedOnUtc;
        entity.mustChangePassword = mustChangePassword;
        entity.createdOnUtc = createdOnUtc;
        return entity;
    }
}
