package com.quinnbank.core.cif.application.port.out;

import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;

import java.util.Optional;
import java.util.UUID;

public interface CustomerReadPort {
    Optional<GetCustomerByIdResult> findById(UUID customerId);
}
