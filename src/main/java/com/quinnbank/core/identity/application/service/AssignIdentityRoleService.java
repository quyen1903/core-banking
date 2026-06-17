package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.IdentityRoleNotFoundException;
import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.application.command.AssignIdentityRoleCommand;
import com.quinnbank.core.identity.application.port.in.AssignIdentityRoleUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.port.out.RoleRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.Role;
import com.quinnbank.core.identity.domain.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignIdentityRoleService implements AssignIdentityRoleUseCase {

    private final IdentityUserRepositoryPort identityUserRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserRoleRepositoryPort userRoleRepository;
    private final Clock clock;

    @Override
    @Transactional
    public void assignRole(AssignIdentityRoleCommand command) {
        if (command == null || command.publicId() == null) {
            throw IdentityUserNotFoundException.byPublicId(null);
        }
        if (command.roleCode() == null) {
            throw IdentityRoleNotFoundException.byCode(null);
        }

        IdentityUser user = identityUserRepository.findByPublicId(command.publicId())
                .orElseThrow(() -> IdentityUserNotFoundException.byPublicId(command.publicId()));
        Role role = roleRepository.findByCode(command.roleCode())
                .orElseThrow(() -> IdentityRoleNotFoundException.byCode(command.roleCode()));

        if (userRoleRepository.existsByIdentityUserIdAndRoleId(user.id(), role.id())) {
            return;
        }

        userRoleRepository.save(UserRole.assign(user.id(), role, LocalDateTime.now(clock)));
    }
}
