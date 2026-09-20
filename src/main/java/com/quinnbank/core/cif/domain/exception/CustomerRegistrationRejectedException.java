package com.quinnbank.core.cif.domain.exception;

public class CustomerRegistrationRejectedException extends IllegalArgumentException {
    public CustomerRegistrationRejectedException(String message) {
        super(message);
    }
}
