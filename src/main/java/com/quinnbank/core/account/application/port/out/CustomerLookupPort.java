package com.quinnbank.core.account.application.port.out;

import java.util.UUID;

public interface CustomerLookupPort {

    void requireActiveCustomer(UUID customerId);
}
