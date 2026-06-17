package com.quinnbank.core.ledger.application.exception;

public class LedgerPostingConflictException extends RuntimeException {

    public LedgerPostingConflictException(String message) {
        super(message);
    }
}
