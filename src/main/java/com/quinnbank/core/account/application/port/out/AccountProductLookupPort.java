package com.quinnbank.core.account.application.port.out;

import com.quinnbank.core.account.domain.model.AccountProduct;

import java.util.Optional;

public interface AccountProductLookupPort {

    Optional<AccountProduct> findActiveOrInactiveByCode(String productCode);
}
