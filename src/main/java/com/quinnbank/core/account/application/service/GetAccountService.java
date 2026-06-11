package com.quinnbank.core.account.application.service;

import com.quinnbank.core.account.application.exception.AccountNotFoundException;
import com.quinnbank.core.account.application.port.in.GetAccountUseCase;
import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.application.query.GetAccountByIdQuery;
import com.quinnbank.core.account.application.result.AccountSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAccountService implements GetAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public AccountSnapshot getById(GetAccountByIdQuery query) {
        if (query == null || query.accountId() == null) {
            throw AccountNotFoundException.byId(null);
        }

        return accountRepositoryPort.findById(query.accountId())
                .map(AccountSnapshot::from)
                .orElseThrow(() -> AccountNotFoundException.byId(query.accountId()));
    }
}
