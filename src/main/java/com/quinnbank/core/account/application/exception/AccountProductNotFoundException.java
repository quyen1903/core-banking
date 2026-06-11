package com.quinnbank.core.account.application.exception;

public class AccountProductNotFoundException extends RuntimeException {

    public AccountProductNotFoundException(String message) {
        super(message);
    }
}
