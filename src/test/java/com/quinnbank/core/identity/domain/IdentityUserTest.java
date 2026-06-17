package com.quinnbank.core.identity.domain;

import com.quinnbank.core.identity.domain.exception.IdentityUserCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityOwnerType;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.IdentityUserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityUserTest {

    @Test
    void createBuildsIdentityUserWithPendingActivationStatus() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 18, 10, 0);

        IdentityUser user = IdentityUser.create(
                IdentityOwnerType.CUSTOMER,
                1001L,
                "  QUYEN.NGUYEN  ",
                "  QUYEN@example.invalid  ",
                "  +84901234567  ",
                createdAt
        );

        assertThat(user.id()).isNull();
        assertThat(user.publicId()).isNotNull();
        assertThat(user.ownerType()).isEqualTo(IdentityOwnerType.CUSTOMER);
        assertThat(user.ownerId()).isEqualTo(1001L);
        assertThat(user.username()).isEqualTo("quyen.nguyen");
        assertThat(user.email()).isEqualTo("quyen@example.invalid");
        assertThat(user.phoneNumber()).isEqualTo("+84901234567");
        assertThat(user.status()).isEqualTo(IdentityUserStatus.PENDING_ACTIVATION);
        assertThat(user.pullDomainEvents()).hasSize(1);
    }

    @Test
    void createRejectsIdentityUserWithoutOwner() {
        assertThatThrownBy(() -> IdentityUser.create(
                IdentityOwnerType.CUSTOMER,
                null,
                "quyen.nguyen",
                null,
                null,
                LocalDateTime.of(2026, 6, 18, 10, 0)
        ))
                .isInstanceOf(IdentityUserCreationRejectedException.class)
                .hasMessage("identity owner id is required");
    }
}
