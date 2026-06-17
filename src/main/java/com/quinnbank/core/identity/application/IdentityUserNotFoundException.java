package com.quinnbank.core.identity.application;

import java.util.UUID;

public class IdentityUserNotFoundException extends RuntimeException {

    private IdentityUserNotFoundException(String message) {
        super(message);
    }

    public static IdentityUserNotFoundException byPublicId(UUID publicId) {
        return new IdentityUserNotFoundException("identity user was not found: " + publicId);
    }
}
