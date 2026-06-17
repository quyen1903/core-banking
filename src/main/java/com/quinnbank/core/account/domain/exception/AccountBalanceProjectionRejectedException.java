package com.quinnbank.core.account.domain.exception;

public class AccountBalanceProjectionRejectedException extends RuntimeException {

    public AccountBalanceProjectionRejectedException(String message) {
        super(message);
    }
}
