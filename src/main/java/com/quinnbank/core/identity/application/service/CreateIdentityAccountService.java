package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.DuplicateIdentityEmailException;
import com.quinnbank.core.identity.application.DuplicateIdentityUsernameException;
import com.quinnbank.core.identity.application.command.CreateIdentityAccountCommand;
import com.quinnbank.core.identity.application.port.in.CreateIdentityAccountUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityAccountRepositoryPort;
import com.quinnbank.core.identity.application.port.out.IdentitySubjectLookupPort;
import com.quinnbank.core.identity.application.result.IdentityAccountSnapshot;
import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityAccount;
import com.quinnbank.core.identity.domain.model.IdentitySubjectType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateIdentityAccountService implements CreateIdentityAccountUseCase {

    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_LENGTH = 128;

    private final IdentityAccountRepositoryPort identityAccountRepository;
    private final IdentitySubjectLookupPort identitySubjectLookup;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public IdentityAccountSnapshot create(CreateIdentityAccountCommand command) {
        validate(command);

        String username = IdentityAccount.normalizeUsername(command.username());
        String email = IdentityAccount.normalizeEmail(command.email());

        if (identityAccountRepository.existsByUsername(username)) {
            throw new DuplicateIdentityUsernameException("identity username already exists");
        }
        if (email != null && identityAccountRepository.existsByEmail(email)) {
            throw new DuplicateIdentityEmailException("identity email already exists");
        }

        identitySubjectLookup.requireActiveSubject(command.subjectType(), command.subjectId());

        IdentityAccount identityAccount = IdentityAccount.create(
                username,
                email,
                command.subjectType(),
                command.subjectId(),
                passwordEncoder.encode(command.initialPassword()),
                LocalDateTime.now(clock)
        );

        return IdentityAccountSnapshot.from(identityAccountRepository.save(identityAccount));
    }

    private static void validate(CreateIdentityAccountCommand command) {
        if (command == null) {
            throw new IdentityAccountCreationRejectedException("create identity account command is required");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IdentityAccountCreationRejectedException("identity username is required");
        }
        if (command.subjectType() == null) {
            throw new IdentityAccountCreationRejectedException("identity subject type is required");
        }
        if ((command.subjectType() == IdentitySubjectType.EMPLOYEE || command.subjectType() == IdentitySubjectType.CUSTOMER)
                && command.subjectId() == null) {
            throw new IdentityAccountCreationRejectedException("identity subject id is required");
        }
        if ((command.subjectType() == IdentitySubjectType.SERVICE_ACCOUNT || command.subjectType() == IdentitySubjectType.SYSTEM)
                && command.subjectId() != null) {
            throw new IdentityAccountCreationRejectedException("service and system identities must not reference employee or customer subjects");
        }
        if (command.initialPassword() == null || command.initialPassword().isBlank()) {
            throw new IdentityAccountCreationRejectedException("initial password is required");
        }
        if (command.initialPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IdentityAccountCreationRejectedException("initial password is too short");
        }
        if (command.initialPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new IdentityAccountCreationRejectedException("initial password is too long");
        }
    }
}
