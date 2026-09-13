package com.quinnbank.core.cif.adapter.in.response;

import java.util.UUID;

import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;
import com.quinnbank.core.cif.domain.enums.CustomerStatus;

public record RegisterCustomerResponse(
    UUID id,
    CustomerStatus status
) {

    public static RegisterCustomerResponse from(RegisterCustomerResult customer) {
        return new RegisterCustomerResponse(
            customer.customerId(),
            customer.status()
        );
    }
}
