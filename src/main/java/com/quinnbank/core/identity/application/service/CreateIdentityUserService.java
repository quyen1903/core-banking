package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.DuplicateIdentityEmailException;
import com.quinnbank.core.identity.application.DuplicateIdentityPhoneNumberException;
import com.quinnbank.core.identity.application.DuplicateIdentityUsernameException;
import com.quinnbank.core.identity.application.command.CreateIdentityUserCommand;
import com.quinnbank.core.identity.application.port.in.CreateIdentityUserUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;
import com.quinnbank.core.identity.domain.exception.IdentityUserCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateIdentityUserService implements CreateIdentityUserUseCase {

    private final IdentityUserRepositoryPort identityUserRepository;
    private final Clock clock;

    @Override
    @Transactional
    public IdentityUserSnapshot create(CreateIdentityUserCommand command) {
        validate(command);

        String username = IdentityUser.normalizeUsername(command.username());
        String email = IdentityUser.normalizeEmail(command.email());
        String phoneNumber = IdentityUser.normalizePhoneNumber(command.phoneNumber());

        if (identityUserRepository.existsByUsername(username)) {
            throw new DuplicateIdentityUsernameException("identity username already exists");
        }
        if (email != null && identityUserRepository.existsByEmail(email)) {
            throw new DuplicateIdentityEmailException("identity email already exists");
        }
        if (phoneNumber != null && identityUserRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateIdentityPhoneNumberException("identity phone number already exists");
        }

        IdentityUser identityUser = IdentityUser.create(
                command.ownerType(),
                command.ownerId(),
                username,
                email,
                phoneNumber,
                LocalDateTime.now(clock)
        );

        return IdentityUserSnapshot.from(identityUserRepository.save(identityUser), Set.of());
    }

    private static void validate(CreateIdentityUserCommand command) {
        if (command == null) {
            throw new IdentityUserCreationRejectedException("create identity user command is required");
        }
        if (command.ownerType() == null) {
            throw new IdentityUserCreationRejectedException("identity owner type is required");
        }
        if (command.ownerId() == null || command.ownerId() <= 0) {
            throw new IdentityUserCreationRejectedException("identity owner id is required");
        }
        if (command.username() == null || command.username().isBlank()) {
            throw new IdentityUserCreationRejectedException("identity username is required");
        }
    }
}
