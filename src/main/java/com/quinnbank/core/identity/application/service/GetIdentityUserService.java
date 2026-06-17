package com.quinnbank.core.identity.application.service;

import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.application.port.in.GetIdentityUserUseCase;
import com.quinnbank.core.identity.application.port.out.IdentityUserRepositoryPort;
import com.quinnbank.core.identity.application.port.out.UserRoleRepositoryPort;
import com.quinnbank.core.identity.application.query.GetIdentityUserByPublicIdQuery;
import com.quinnbank.core.identity.application.result.IdentityUserSnapshot;
import com.quinnbank.core.identity.domain.model.IdentityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetIdentityUserService implements GetIdentityUserUseCase {

    private final IdentityUserRepositoryPort identityUserRepository;
    private final UserRoleRepositoryPort userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public IdentityUserSnapshot getByPublicId(GetIdentityUserByPublicIdQuery query) {
        if (query == null || query.publicId() == null) {
            throw IdentityUserNotFoundException.byPublicId(null);
        }

        IdentityUser user = identityUserRepository.findByPublicId(query.publicId())
                .orElseThrow(() -> IdentityUserNotFoundException.byPublicId(query.publicId()));

        return IdentityUserSnapshot.from(user, userRoleRepository.findRoleCodesByIdentityUserId(user.id()));
    }
}
