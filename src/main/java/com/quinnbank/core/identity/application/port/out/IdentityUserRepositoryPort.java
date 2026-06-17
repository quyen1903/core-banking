package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.IdentityUser;

import java.util.Optional;
import java.util.UUID;

public interface IdentityUserRepositoryPort {

    IdentityUser save(IdentityUser identityUser);

    Optional<IdentityUser> findByPublicId(UUID publicId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
