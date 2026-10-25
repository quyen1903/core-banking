package com.quinnbank.core.auth.application.port.out;

import com.quinnbank.core.auth.domain.model.IdentityUser;

public interface IdentityUserWritePort {
    boolean existsByUsernameNormalized(String usernameNormalized);
    void save(IdentityUser identityUser);
}
