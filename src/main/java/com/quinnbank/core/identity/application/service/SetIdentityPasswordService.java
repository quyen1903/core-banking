package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.application.command.SetIdentityPasswordCommand;
import com.quinnbank.core.identity.application.port.in.SetIdentityPasswordUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityCredentialRepositoryPort;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.domain.exception.IdentityCredentialRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityCredential;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SetIdentityPasswordService implements SetIdentityPasswordUseCase {

    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_LENGTH = 128;

    private final IdentityUserRepositoryPort identityUserRepository;
    private final IdentityCredentialRepositoryPort identityCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public void setPassword(SetIdentityPasswordCommand command) {
        validate(command);

        IdentityUser user = identityUserRepository.findByPublicId(command.publicId())
                .orElseThrow(() -> IdentityUserNotFoundException.byPublicId(command.publicId()));
        if (user.id() == null) {
            throw new IdentityCredentialRejectedException("identity user id is required for credential");
        }

        String encodedPassword = passwordEncoder.encode(command.newPassword());
        LocalDateTime changedOnUtc = LocalDateTime.now(clock);

        IdentityCredential credential = identityCredentialRepository.findPasswordCredentialByIdentityUserId(user.id())
                .map(existing -> existing.replacePassword(
                        encodedPassword,
                        command.mustChangePassword(),
                        changedOnUtc
                ))
                .orElseGet(() -> IdentityCredential.password(
                        user.id(),
                        encodedPassword,
                        command.mustChangePassword(),
                        changedOnUtc
                ));

        identityCredentialRepository.save(credential);
    }

    private static void validate(SetIdentityPasswordCommand command) {
        if (command == null || command.publicId() == null) {
            throw IdentityUserNotFoundException.byPublicId(null);
        }
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            throw new IdentityCredentialRejectedException("new password is required");
        }
        if (command.newPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IdentityCredentialRejectedException("new password is too short");
        }
        if (command.newPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new IdentityCredentialRejectedException("new password is too long");
        }
    }
}
