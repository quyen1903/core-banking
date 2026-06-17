package com.quinnbank.core.identity.domain.exception;

public class IdentityCredentialRejectedException extends RuntimeException {

    public IdentityCredentialRejectedException(String message) {
        super(message);
    }
}
