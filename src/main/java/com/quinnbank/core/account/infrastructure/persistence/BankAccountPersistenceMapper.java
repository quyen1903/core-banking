package com.quinnbank.core.account.infrastructure.persistence;

import com.quinnbank.core.account.domain.model.AccountNumber;
import com.quinnbank.core.account.domain.model.BankAccount;
import com.quinnbank.core.account.domain.model.Money;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
class BankAccountPersistenceMapper {

    BankAccountJpaEntity toEntity(BankAccount account) {
        return BankAccountJpaEntity.create(
                account.id(),
                account.accountNumber().value(),
                account.customerId(),
                account.productId(),
                account.currentBalance().currency().getCurrencyCode(),
                account.availableBalance().amount(),
                account.currentBalance().amount(),
                account.status(),
                account.openingIdempotencyKey(),
                account.openingRequestFingerprint(),
                account.createdAt(),
                account.updatedAt(),
                account.version()
        );
    }

    BankAccount toDomain(BankAccountJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());

        return BankAccount.restore(
                entity.getId(),
                new AccountNumber(entity.getAccountNumber()),
                entity.getCustomerId(),
                entity.getProductId(),
                new Money(entity.getAvailableBalance(), currency),
                new Money(entity.getCurrentBalance(), currency),
                entity.getStatus(),
                entity.getOpeningIdempotencyKey(),
                entity.getOpeningRequestFingerprint(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }
}
