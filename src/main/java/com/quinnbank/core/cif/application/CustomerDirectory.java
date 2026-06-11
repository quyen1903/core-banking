package com.quinnbank.core.cif.application;

import java.util.Optional;
import java.util.UUID;

public interface CustomerDirectory {

    CustomerSnapshot requireActiveCustomer(UUID customerId);

    Optional<CustomerSnapshot> findByCustomerNumber(String customerNumber);
}
