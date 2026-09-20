package com.quinnbank.core.cif.application.result;

import java.util.UUID;

public record RegisterCustomerResult(
    UUID customerId,
    String status
) {}
