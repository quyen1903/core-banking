package com.quinnbank.core.auth.application.port.out;

import com.quinnbank.core.auth.domain.model.IdentityCredential;

public interface IdentityCredentialWritePort {
    void save (IdentityCredential identityCredential);
}
