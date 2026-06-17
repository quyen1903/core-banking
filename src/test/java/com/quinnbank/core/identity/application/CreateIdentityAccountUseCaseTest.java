package com.quinnbank.core.identity.application;

import com.quinnbank.core.identity.application.command.CreateIdentityAccountCommand;
import com.quinnbank.core.identity.application.port.out.IdentityAccountRepositoryPort;
import com.quinnbank.core.identity.application.port.out.IdentitySubjectLookupPort;
import com.quinnbank.core.identity.application.result.IdentityAccountSnapshot;
import com.quinnbank.core.identity.application.service.CreateIdentityAccountService;
import com.quinnbank.core.identity.domain.model.IdentityAccount;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateIdentityAccountUseCaseTest {

    private final IdentityAccountRepositoryPort identityAccountRepository = mock(IdentityAccountRepositoryPort.class);
    private final IdentitySubjectLookupPort identitySubjectLookup = mock(IdentitySubjectLookupPort.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-16T03:00:00Z"), ZoneOffset.UTC);
    private final CreateIdentityAccountService useCase = new CreateIdentityAccountService(
            identityAccountRepository,
            identitySubjectLookup,
            passwordEncoder,
            clock
    );

    @Test
    void createHashesCredentialAndValidatesSubjectThroughPort() {
        UUID employeeId = UUID.randomUUID();
        when(identityAccountRepository.existsByUsername("branch.operator")).thenReturn(false);
        when(identityAccountRepository.existsByEmail("operator@example.invalid")).thenReturn(false);
        when(passwordEncoder.encode("replace-me-local-only")).thenReturn("{bcrypt}encoded-local-only");
        when(identityAccountRepository.save(any(IdentityAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IdentityAccountSnapshot account = useCase.create(new CreateIdentityAccountCommand(
                "BRANCH.OPERATOR",
                "OPERATOR@example.invalid",
                IdentitySubjectType.EMPLOYEE,
                employeeId,
                "replace-me-local-only"
        ));

        assertThat(account.username()).isEqualTo("branch.operator");
        assertThat(account.subjectType()).isEqualTo(IdentitySubjectType.EMPLOYEE);
        assertThat(account.subjectId()).isEqualTo(employeeId);
        verify(identitySubjectLookup).requireActiveSubject(IdentitySubjectType.EMPLOYEE, employeeId);
        verify(passwordEncoder).encode("replace-me-local-only");
        verify(identityAccountRepository).save(any(IdentityAccount.class));
    }

    @Test
    void createRejectsDuplicateUsernameBeforeCredentialHashing() {
        when(identityAccountRepository.existsByUsername("branch.operator")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(new CreateIdentityAccountCommand(
                "branch.operator",
                null,
                IdentitySubjectType.EMPLOYEE,
                UUID.randomUUID(),
                "replace-me-local-only"
        )))
                .isInstanceOf(DuplicateIdentityUsernameException.class)
                .hasMessage("identity username already exists");

        verify(passwordEncoder, never()).encode(any());
        verify(identitySubjectLookup, never()).requireActiveSubject(any(), any());
        verify(identityAccountRepository, never()).save(any(IdentityAccount.class));
    }
}
