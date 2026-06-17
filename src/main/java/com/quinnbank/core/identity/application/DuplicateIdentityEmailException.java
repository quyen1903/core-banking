package com.quinnbank.core.identity.application;

public class DuplicateIdentityEmailException extends RuntimeException {

    public DuplicateIdentityEmailException(String message) {
        super(message);
    }
}
