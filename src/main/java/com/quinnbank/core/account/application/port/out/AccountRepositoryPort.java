package com.quinnbank.core.account.application.port.out;

import com.quinnbank.core.account.domain.model.BankAccount;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepositoryPort {

    BankAccount save(BankAccount account);

    Optional<BankAccount> findById(UUID accountId);
}
