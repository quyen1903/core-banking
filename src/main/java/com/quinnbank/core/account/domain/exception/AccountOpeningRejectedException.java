package com.quinnbank.core.account.domain.exception;

public class AccountOpeningRejectedException extends RuntimeException {

    public AccountOpeningRejectedException(String message) {
        super(message);
    }
}
