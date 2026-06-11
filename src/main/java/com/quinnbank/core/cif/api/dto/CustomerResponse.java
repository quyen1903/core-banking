package com.quinnbank.core.cif.api.dto;

import com.quinnbank.core.cif.application.CustomerSnapshot;

import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String customerNumber,
        String fullName,
        String email,
        String phone,
        String status,
        String kycStatus,
        String riskRating
) {

    public static CustomerResponse from(CustomerSnapshot customer) {
        return new CustomerResponse(
                customer.id(),
                customer.customerNumber(),
                customer.fullName(),
                customer.email(),
                customer.phone(),
                customer.status().name(),
                customer.kycStatus().name(),
                customer.riskRating().name()
        );
    }
}
