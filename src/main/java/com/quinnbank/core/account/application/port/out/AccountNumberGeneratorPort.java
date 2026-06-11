package com.quinnbank.core.account.application.port.out;

import com.quinnbank.core.account.domain.model.AccountNumber;

public interface AccountNumberGeneratorPort {

    AccountNumber nextAccountNumber();
}
