package com.quinnbank.core.cif.application;

public class DuplicateCustomerEmailException extends RuntimeException {

    public DuplicateCustomerEmailException(String message) {
        super(message);
    }
}
