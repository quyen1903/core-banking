package com.quinnbank.core.cif.application.query;

import java.util.Objects;
import java.util.UUID;

public record GetCustomerByIdQuery(UUID customerId) {
    public GetCustomerByIdQuery {
        Objects.requireNonNull(customerId, "customer id is required");
    }
}
