package com.quinnbank.core.cif.application.port.in;

import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;

public interface GetCustomerByIdUseCase {
    GetCustomerByIdResult getCustomerById(GetCustomerByIdQuery query);
}
