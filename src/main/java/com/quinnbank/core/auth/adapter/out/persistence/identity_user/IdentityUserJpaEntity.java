package com.quinnbank.core.auth.adapter.out.persistence.identity_user;

import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;
import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing an identity user in the system.
 * this file describe how Java object is mapped to the database table "identity_user" and its columns.
 */
@Entity
@Table(
    name = "identity_user",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_identity_user_public_id", columnNames = "public_id"),
        @UniqueConstraint(name = "uq_identity_user_username", columnNames = "username_normalized")
    },
    indexes = @Index(name = "ix_identity_user_owner", columnList = "owner_type, owner_id"),
    check = {
        @CheckConstraint(name = "ck_identity_user_username", constraint = "length(btrim(username)) > 0"),
        @CheckConstraint(name = "ck_identity_user_username_normalized", constraint = """
                username_normalized ~ '^[a-z0-9._@+-]+$'
                and username_normalized = lower(btrim(username) collate "C")
                """),
        @CheckConstraint(name = "ck_identity_user_security_version", constraint = "security_version >= 0"),
        @CheckConstraint(name = "ck_identity_user_version", constraint = "version >= 0")
    }
)
public class IdentityUserJpaEntity {
    @Id
    UUID id;

    @Column(name = "public_id", nullable = false, updatable = false)
    UUID publicId;

    @Column(name = "username", nullable = false, length = 100)
    String username;

    @Column(
        name = "username_normalized",
        nullable = false,
        length = 100,
        columnDefinition = "varchar(100) collate \"C\"")
    String usernameNormalized;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, updatable = false, length = 16)
    IdentityOwnerType ownerType;

    @Column(name = "owner_id", nullable = false, updatable = false)
    UUID ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    IdentityStatus status;

    @Column(name = "security_version", nullable = false)
    long securityVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;

    protected IdentityUserJpaEntity() {}
}
