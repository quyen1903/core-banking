package com.quinnbank.core.cif.application.port.in;

import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;

public interface RegisterCustomerUseCase {
    RegisterCustomerResult registerCustomer(RegisterCustomerCommand command);
}
