package com.quinnbank.core.cif.application.port.in;

import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;

public interface CustomerCommandUseCase {
    RegisterCustomerResult registerCustomer (RegisterCustomerCommand command);
}
