package com.quinnbank.core.account.application.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public static AccountNotFoundException byId(UUID accountId) {
        return new AccountNotFoundException("account not found");
    }

    private AccountNotFoundException(String message) {
        super(message);
    }
}
