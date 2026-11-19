package com.quinnbank.core.auth.application.port.out;

import java.util.Optional;

import com.quinnbank.core.auth.domain.model.IdentityUser;

public interface IdentityUserWritePort {
    boolean existsByUsernameNormalized(String usernameNormalized);
    Optional<IdentityUser> findByUsernameNormalized(String usernameNormalized);
    void save(IdentityUser identityUser);
}
