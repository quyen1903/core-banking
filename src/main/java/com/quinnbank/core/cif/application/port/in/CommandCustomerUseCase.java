package com.quinnbank.core.cif.application.port.in;

import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.command.UpdateCustomerCommand;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.result.UpdateCustomerResult;

public interface CommandCustomerUseCase {
    RegisterCustomerResult registerCustomer(RegisterCustomerCommand command);
    UpdateCustomerResult updateCustomer(UpdateCustomerCommand command);
}
