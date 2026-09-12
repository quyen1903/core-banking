package com.quinnbank.core.cif.application.service;

import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.contract.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.contract.result.RegisterCustomerResult;;;

public class CustomerRegisterService implements RegisterCustomerUseCase {
    
    public RegisterCustomerResult registerCustomer(RegisterCustomerCommand command) {
        //check if the customer already exists
        
        return new RegisterCustomerResult();
    }
}
