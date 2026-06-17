package com.quinnbank.core.identity.application.port.out;

import com.quinnbank.core.identity.domain.model.IdentityCredential;

import java.util.Optional;

public interface IdentityCredentialRepositoryPort {

    IdentityCredential save(IdentityCredential credential);

    Optional<IdentityCredential> findPasswordCredentialByIdentityUserId(Long identityUserId);
}
