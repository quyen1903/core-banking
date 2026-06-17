package com.quinnbank.core.ledger.domain.exception;

public class LedgerPostingRejectedException extends RuntimeException {

    public LedgerPostingRejectedException(String message) {
        super(message);
    }
}
