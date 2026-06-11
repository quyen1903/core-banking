package com.quinnbank.core.account.infrastructure.persistence;

import com.quinnbank.core.account.application.port.out.AccountProductLookupPort;
import com.quinnbank.core.account.domain.model.AccountProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class AccountProductPersistenceAdapter implements AccountProductLookupPort {

    private final SpringDataAccountProductRepository repository;
    private final AccountProductPersistenceMapper mapper;

    @Override
    public Optional<AccountProduct> findActiveOrInactiveByCode(String productCode) {
        return repository.findByCode(productCode)
                .map(mapper::toDomain);
    }
}
