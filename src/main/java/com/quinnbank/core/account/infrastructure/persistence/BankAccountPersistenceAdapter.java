package com.quinnbank.core.account.infrastructure.persistence;

import com.quinnbank.core.account.application.port.out.AccountRepositoryPort;
import com.quinnbank.core.account.application.port.out.IdempotencyPort;
import com.quinnbank.core.account.application.result.AccountOpeningIdempotencyResult;
import com.quinnbank.core.account.domain.model.BankAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class BankAccountPersistenceAdapter implements AccountRepositoryPort, IdempotencyPort {

    private final SpringDataBankAccountRepository repository;
    private final BankAccountPersistenceMapper mapper;

    @Override
    public BankAccount save(BankAccount account) {
        BankAccountJpaEntity saved = repository.save(mapper.toEntity(account));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BankAccount> findById(UUID accountId) {
        return repository.findById(accountId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AccountOpeningIdempotencyResult> findAccountOpening(String idempotencyKey) {
        return repository.findByOpeningIdempotencyKey(idempotencyKey)
                .map(entity -> new AccountOpeningIdempotencyResult(
                        entity.getOpeningIdempotencyKey(),
                        entity.getOpeningRequestFingerprint(),
                        entity.getId()
                ));
    }
}
