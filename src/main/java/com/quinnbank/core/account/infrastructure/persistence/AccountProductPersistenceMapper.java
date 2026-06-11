package com.quinnbank.core.account.infrastructure.persistence;

import com.quinnbank.core.account.domain.model.AccountProduct;
import com.quinnbank.core.account.domain.model.Money;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
class AccountProductPersistenceMapper {

    AccountProduct toDomain(AccountProductJpaEntity entity) {
        Currency currency = Currency.getInstance(entity.getCurrency());

        return AccountProduct.restore(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                currency,
                new Money(entity.getMinBalance(), currency),
                entity.getInterestRate(),
                new Money(entity.getMonthlyFee(), currency),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
