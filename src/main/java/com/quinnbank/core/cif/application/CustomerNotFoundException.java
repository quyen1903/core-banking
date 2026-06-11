package com.quinnbank.core.cif.application;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    private CustomerNotFoundException(String message) {
        super(message);
    }

    public static CustomerNotFoundException byId(UUID customerId) {
        return new CustomerNotFoundException("customer not found");
    }

    public static CustomerNotFoundException activeCustomerRequired(UUID customerId) {
        return new CustomerNotFoundException("active customer required");
    }
}
