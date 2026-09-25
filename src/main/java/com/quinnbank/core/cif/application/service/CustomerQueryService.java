package com.quinnbank.core.cif.application.service;

import com.quinnbank.core.cif.application.CustomerNotFoundException;
import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;

import java.util.Objects;

public final class CustomerQueryService implements GetCustomerByIdUseCase {
    private final CustomerReadPort customers;

    public CustomerQueryService(CustomerReadPort customers) {
        this.customers = Objects.requireNonNull(customers);
    }

    @Override
    public GetCustomerByIdResult getCustomerById(GetCustomerByIdQuery query) {
        Objects.requireNonNull(query, "customer query is required");
        return customers
            .findById(query.customerId())
            .orElseThrow(() -> CustomerNotFoundException.byId(query.customerId()));
    }
}
