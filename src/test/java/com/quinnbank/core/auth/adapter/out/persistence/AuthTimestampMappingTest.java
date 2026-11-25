package com.quinnbank.core.auth.adapter.out.persistence;

import com.quinnbank.core.auth.adapter.out.persistence.identity_credential.IdentityCredentialPersistenceMapper;
import com.quinnbank.core.auth.adapter.out.persistence.identity_login_state.IdentityLoginStatePersistenceMapper;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserJpaEntity;
import com.quinnbank.core.auth.adapter.out.persistence.identity_user.IdentityUserPersistenceMapper;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;
import com.quinnbank.core.auth.domain.enums.IdentityStatus;
import com.quinnbank.core.auth.domain.model.IdentityCredential;
import com.quinnbank.core.auth.domain.model.IdentityLoginState;
import com.quinnbank.core.auth.domain.model.IdentityUser;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTimestampMappingTest {
    private static final Instant CREATED_AT = Instant.parse("2023-02-01T08:00:00.123456Z");
    private static final Instant UPDATED_AT = Instant.parse("2024-03-15T15:30:00.654321Z");
    private final UUID userId = UUID.randomUUID();
    private final UUID publicId = UUID.randomUUID();
    private final IdentityUserJpaEntity userRow = IdentityUserPersistenceMapper.toNewEntity(
        IdentityUser.restore(userId, publicId, "synthetic.user", IdentityOwnerType.CUSTOMER,
            UUID.randomUUID(), IdentityStatus.ACTIVE, 1L, CREATED_AT, UPDATED_AT, 2L));

    @Test
    void readingUserPreservesBothStoredTimestamps() {
        IdentityUser user = IdentityUserPersistenceMapper.toDomain(userRow);

        assertThat(user.getCreateAt()).isEqualTo(CREATED_AT);
        assertThat(user.getUpdateAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void readingCredentialPreservesPasswordChangeAndRecordTimestamps() {
        Instant passwordChangedAt = CREATED_AT.plusSeconds(3600);
        var credential = IdentityCredential.restore(userId, publicId, "local-only-hash",
            passwordChangedAt, true, CREATED_AT, UPDATED_AT);
        var row = IdentityCredentialPersistenceMapper.toNewEntity(credential, userRow);

        IdentityCredential restored = IdentityCredentialPersistenceMapper.toDomain(row);

        assertThat(restored.getIdentityPublicId()).isEqualTo(publicId);
        assertThat(restored.getPasswordChangedAt()).isEqualTo(passwordChangedAt);
        assertThat(restored.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(restored.getUpdatedAt()).isEqualTo(UPDATED_AT);
    }

    @Test
    void readingLoginStatePreservesRecordedTimesAndAbsentAuthentication() {
        Instant firstFailure = UPDATED_AT.minusSeconds(120);
        Instant lastFailure = UPDATED_AT.minusSeconds(1);
        Instant lockedUntil = UPDATED_AT.plusSeconds(900);
        var state = IdentityLoginState.restore(userId, 2, firstFailure, lastFailure, lockedUntil,
            null, CREATED_AT, UPDATED_AT);
        var row = IdentityLoginStatePersistenceMapper.toNewEntity(state, userRow);

        IdentityLoginState restored = IdentityLoginStatePersistenceMapper.toNewDomain(row);

        assertThat(restored.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(restored.getUpdatedAt()).isEqualTo(UPDATED_AT);
        assertThat(restored.getFailureWindowStartedAt()).isEqualTo(firstFailure);
        assertThat(restored.getLastFailedAt()).isEqualTo(lastFailure);
        assertThat(restored.getLockedUntil()).isEqualTo(lockedUntil);
        assertThat(restored.getLastAuthenticatedAt()).isNull();
    }

    @Test
    void newRecordsUseTheirCreationTimeForTheirInitialUpdate() {
        var user = IdentityUser.create("synthetic.user", IdentityOwnerType.CUSTOMER, UUID.randomUUID());
        var credential = IdentityCredential.create(user.getId(), user.getPublicId(), "local-only-hash", false);
        var state = IdentityLoginState.create(user.getId());

        assertThat(user.getUpdateAt()).isNotNull().isEqualTo(user.getCreateAt());
        assertThat(credential.getUpdatedAt()).isNotNull().isEqualTo(credential.getCreatedAt());
        assertThat(credential.getPasswordChangedAt()).isEqualTo(credential.getCreatedAt());
        assertThat(state.getUpdatedAt()).isNotNull().isEqualTo(state.getCreatedAt());
    }
}
