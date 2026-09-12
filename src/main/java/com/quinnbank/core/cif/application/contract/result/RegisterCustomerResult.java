package com.quinnbank.core.cif.application.contract.result;

import java.util.UUID;
import com.quinnbank.core.cif.domain.enums.CustomerStatus;

public record RegisterCustomerResult(
    UUID customerId,
    CustomerStatus status
) {}
