package com.quinnbank.core.identity.application;

public class DuplicateIdentityUsernameException extends RuntimeException {

    public DuplicateIdentityUsernameException(String message) {
        super(message);
    }
}
