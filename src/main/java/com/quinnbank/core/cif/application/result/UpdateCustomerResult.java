package com.quinnbank.core.cif.application.result;

import java.util.UUID;

public record UpdateCustomerResult(
    UUID customerId,
    String status
) {}
