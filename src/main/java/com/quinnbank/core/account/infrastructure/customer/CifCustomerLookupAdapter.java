package com.quinnbank.core.account.infrastructure.customer;

import com.quinnbank.core.account.application.port.out.CustomerLookupPort;
import com.quinnbank.core.cif.application.CustomerDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class CifCustomerLookupAdapter implements CustomerLookupPort {

    private final CustomerDirectory customerDirectory;

    @Override
    public void requireActiveCustomer(UUID customerId) {
        customerDirectory.requireActiveCustomer(customerId);
    }
}
