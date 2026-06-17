package com.quinnbank.core.identity.domain;

import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityAccount;
import com.quinnbank.core.identity.domain.model.IdentityAccountStatus;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentityAccountTest {

    @Test
    void createBuildsLoginIdentityWithoutCustomerOrBankAccountData() {
        UUID employeeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 16, 10, 0);

        IdentityAccount account = IdentityAccount.create(
                "  BRANCH.OPERATOR  ",
                "  OPERATOR@example.invalid  ",
                IdentitySubjectType.EMPLOYEE,
                employeeId,
                "{bcrypt}replace-me-local-only",
                createdAt
        );

        assertThat(account.id()).isNotNull();
        assertThat(account.username()).isEqualTo("branch.operator");
        assertThat(account.email()).isEqualTo("operator@example.invalid");
        assertThat(account.subjectType()).isEqualTo(IdentitySubjectType.EMPLOYEE);
        assertThat(account.subjectId()).isEqualTo(employeeId);
        assertThat(account.status()).isEqualTo(IdentityAccountStatus.ACTIVE);
        assertThat(account.credential().passwordHash()).isEqualTo("{bcrypt}replace-me-local-only");
        assertThat(account.pullDomainEvents()).hasSize(1);
    }

    @Test
    void createRejectsCustomerIdentityWithoutSubjectId() {
        assertThatThrownBy(() -> IdentityAccount.create(
                "customer.login",
                null,
                IdentitySubjectType.CUSTOMER,
                null,
                "{bcrypt}replace-me-local-only",
                LocalDateTime.of(2026, 6, 16, 10, 0)
        ))
                .isInstanceOf(IdentityAccountCreationRejectedException.class)
                .hasMessage("identity subject id is required");
    }
}
