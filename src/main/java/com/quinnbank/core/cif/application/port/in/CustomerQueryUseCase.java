package com.quinnbank.core.cif.application.port.in;

import com.quinnbank.core.cif.application.contract.result.GetCustomerByIdResult;
import java.util.UUID;

public interface CustomerQueryUseCase {
    GetCustomerByIdResult getCustomerById(UUID customerId);
}
