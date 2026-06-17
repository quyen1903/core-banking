package com.quinnbank.core.identity.domain.exception;

public class IdentityUserCreationRejectedException extends RuntimeException {

    public IdentityUserCreationRejectedException(String message) {
        super(message);
    }
}
