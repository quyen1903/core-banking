package com.quinnbank.core.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.quinnbank.core.auth.domain.model.IdentityLoginState;

public interface IdentityLoginStateWritePort {
    void save(IdentityLoginState loginState);
    Optional<IdentityLoginState> findByUserId(UUID userId);
}
