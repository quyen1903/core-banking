package com.quinnbank.core.cif.application.port.out;

import com.quinnbank.core.cif.domain.Customer;
public interface CustomerPort {
    Customer save(Customer customer);
}
