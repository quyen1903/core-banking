package com.quinnbank.core.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.quinnbank.core.auth.domain.model.IdentityCredential;

public interface IdentityCredentialWritePort {
    void save (IdentityCredential identityCredential);
    Optional<IdentityCredential> findByUserId(UUID userId);

}
