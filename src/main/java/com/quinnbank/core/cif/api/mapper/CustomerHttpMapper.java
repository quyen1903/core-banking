package com.quinnbank.core.cif.api.mapper;

import com.quinnbank.core.cif.api.dto.request.RegisterCustomerRequest;
import com.quinnbank.core.cif.api.dto.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;

public final class CustomerHttpMapper {

    private CustomerHttpMapper() {
    }

    public static RegisterCustomerCommand toCommand(RegisterCustomerRequest request) {
        return new RegisterCustomerCommand(
            request.firstName(),
            request.lastName(),
            request.email(),
            request.phone(),
            request.officeId(),
            request.externalId()
        );
    }

    public static RegisterCustomerResponse toResponse(RegisterCustomerResult result) {
        return new RegisterCustomerResponse(result.customerId(), result.status());
    }
}
