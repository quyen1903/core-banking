package com.quinnbank.core.auth.adapter.out.persistence.identity_credential;

import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "identity_credential", check = {
        @CheckConstraint(name = "ck_identity_credential_hash", constraint = "length(btrim(password_hash)) > 0"),
        @CheckConstraint(name = "ck_identity_credential_version", constraint = "version >= 0")
})
public class IdentityCredentialJpaEntity {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id", 
        nullable = false, 
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_identity_credential_user"))
    IdentityUserJpaEntity user;

    @Column(name = "password_hash", nullable = false, length = 512)
    String passwordHash;

    @Column(name = "password_changed_at", nullable = false)
    Instant passwordChangedAt;

    @Column(name = "must_change_password", nullable = false)
    boolean mustChangePassword = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;

    protected IdentityCredentialJpaEntity() {
    }
}
