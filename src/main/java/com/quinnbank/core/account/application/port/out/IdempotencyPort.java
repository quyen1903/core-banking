package com.quinnbank.core.account.application.port.out;

import com.quinnbank.core.account.application.result.AccountOpeningIdempotencyResult;

import java.util.Optional;

public interface IdempotencyPort {

    Optional<AccountOpeningIdempotencyResult> findAccountOpening(String idempotencyKey);
}
