package com.quinnbank.core.cif.application.port.out;

import com.quinnbank.core.cif.domain.model.Customer;

public interface CustomerWritePort {
    /** Insert one new customer; duplicate email raises DuplicateCustomerEmailException. */
    void save(Customer customer);

    boolean existsByEmail(String normalizedEmail);
}
