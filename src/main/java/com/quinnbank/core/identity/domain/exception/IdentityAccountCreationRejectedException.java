package com.quinnbank.core.identity.domain.exception;

public class IdentityAccountCreationRejectedException extends RuntimeException {

    public IdentityAccountCreationRejectedException(String message) {
        super(message);
    }
}
