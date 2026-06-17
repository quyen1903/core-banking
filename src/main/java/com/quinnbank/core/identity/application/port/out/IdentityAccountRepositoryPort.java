package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.IdentityAccount;

import java.util.Optional;
import java.util.UUID;

public interface IdentityAccountRepositoryPort {

    IdentityAccount save(IdentityAccount identityAccount);

    Optional<IdentityAccount> findById(UUID identityAccountId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
