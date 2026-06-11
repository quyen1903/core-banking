package com.quinnbank.core.account.domain.policy;

import com.quinnbank.core.account.domain.exception.AccountOpeningRejectedException;
import com.quinnbank.core.account.domain.model.AccountProduct;

public class AccountOpeningPolicy {

    public void verify(AccountProduct product) {
        if (product == null) {
            throw new AccountOpeningRejectedException("account product is required");
        }
        if (!product.active()) {
            throw new AccountOpeningRejectedException("account product is inactive");
        }
        if (!product.permitsZeroOpeningBalance()) {
            throw new AccountOpeningRejectedException("account product requires funded opening");
        }
    }
}
