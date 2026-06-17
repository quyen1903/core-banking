package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.application.command.ChangeIdentityUserStatusCommand;
import com.quinnbank.core.identity.application.port.in.ChangeIdentityUserStatusUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;
import com.quinnbank.core.identity.domain.exception.IdentityUserCreationRejectedException;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChangeIdentityUserStatusService implements ChangeIdentityUserStatusUseCase {

    private final IdentityUserRepositoryPort identityUserRepository;
    private final UserRoleRepositoryPort userRoleRepository;
    private final Clock clock;

    @Override
    @Transactional
    public IdentityUserSnapshot changeStatus(ChangeIdentityUserStatusCommand command) {
        if (command == null || command.publicId() == null) {
            throw IdentityUserNotFoundException.byPublicId(null);
        }
        if (command.status() == null) {
            throw new IdentityUserCreationRejectedException("identity user status is required");
        }

        IdentityUser user = identityUserRepository.findByPublicId(command.publicId())
                .orElseThrow(() -> IdentityUserNotFoundException.byPublicId(command.publicId()));
        IdentityUser changed = user.changeStatus(command.status(), LocalDateTime.now(clock));
        IdentityUser saved = identityUserRepository.save(changed);

        return IdentityUserSnapshot.from(saved, userRoleRepository.findRoleCodesByIdentityUserId(saved.id()));
    }
}
