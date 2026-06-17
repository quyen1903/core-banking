package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.IdentityRoleNotFoundException;
import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.application.command.RemoveIdentityRoleCommand;
import com.quinnbank.core.identity.application.port.in.RemoveIdentityRoleUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.port.out.RoleRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import com.quinnbank.core.identity.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RemoveIdentityRoleService implements RemoveIdentityRoleUseCase {

    private final IdentityUserRepositoryPort identityUserRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserRoleRepositoryPort userRoleRepository;

    @Override
    @Transactional
    public void removeRole(RemoveIdentityRoleCommand command) {
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

        userRoleRepository.remove(user.id(), role.id());
    }
}
