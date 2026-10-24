package com.quinnbank.core.auth.adapter.out.persistence.identity_login_state;

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

import java.util.UUID;
import java.time.Instant;

@Entity
@Table(
    name = "identity_login_state", 
    check = {
        @CheckConstraint(name = "ck_identity_login_state_failed_attempts", constraint = "failed_attempts >= 0"),
        @CheckConstraint(name = "ck_identity_login_state_failure_window", constraint = """
            (failed_attempts = 0 and failure_window_started_at is null)
            or (failed_attempts > 0 and failure_window_started_at is not null
            and last_failed_at is not null and last_failed_at >= failure_window_started_at)
            """),
        @CheckConstraint(name = "ck_identity_login_state_version", constraint = "version >= 0")
    }
)
public class IdentityLoginStateJpaEntity {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    UUID userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_identity_login_state_user")
    )
    IdentityUserJpaEntity user;

    @Column(name = "failed_attempts", nullable = false)
    int failedAttempts;

    @Column(name = "failure_window_started_at")
    Instant failureWindowStartedAt;

    @Column(name = "last_failed_at")
    Instant lastFailedAt;

    @Column(name = "locked_until")
    Instant lockedUntil;

    @Column(name = "last_authenticated_at")
    Instant lastAuthenticatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    Long version;

    protected IdentityLoginStateJpaEntity() {}
}
