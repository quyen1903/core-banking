package com.quinnbank.core.account.application.port.in;

import com.quinnbank.core.account.application.query.GetAccountByIdQuery;
import com.quinnbank.core.account.application.result.AccountSnapshot;

public interface GetAccountUseCase {

    AccountSnapshot getById(GetAccountByIdQuery query);
}
