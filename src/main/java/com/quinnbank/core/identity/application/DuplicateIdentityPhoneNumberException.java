package com.quinnbank.core.identity.application;

public class DuplicateIdentityPhoneNumberException extends RuntimeException {

    public DuplicateIdentityPhoneNumberException(String message) {
        super(message);
    }
}
