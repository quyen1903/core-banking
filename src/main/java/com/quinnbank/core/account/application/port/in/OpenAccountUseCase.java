package com.quinnbank.core.account.application.port.in;

import com.quinnbank.core.account.application.command.OpenAccountCommand;
import com.quinnbank.core.account.application.result.AccountSnapshot;

public interface OpenAccountUseCase {

    AccountSnapshot open(OpenAccountCommand command);
}
