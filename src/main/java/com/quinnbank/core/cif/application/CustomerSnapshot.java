package com.quinnbank.core.cif.application;

import com.quinnbank.core.cif.domain.Customer;
import com.quinnbank.core.cif.domain.enums.CustomerStatus;
import com.quinnbank.core.cif.domain.enums.KycStatus;
import com.quinnbank.core.cif.domain.enums.RiskRating;

import java.util.UUID;

public record CustomerSnapshot(
        UUID id,
        String customerNumber,
        String fullName,
        String email,
        String phone,
        CustomerStatus status,
        KycStatus kycStatus,
        RiskRating riskRating
) {
    public static CustomerSnapshot from(Customer customer) {
        return new CustomerSnapshot(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getKycStatus(),
                customer.getRiskRating()
        );
    }
}
