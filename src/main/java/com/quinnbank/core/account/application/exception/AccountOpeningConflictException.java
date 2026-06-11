package com.quinnbank.core.account.application.exception;

public class AccountOpeningConflictException extends RuntimeException {

    public AccountOpeningConflictException(String message) {
        super(message);
    }
}
