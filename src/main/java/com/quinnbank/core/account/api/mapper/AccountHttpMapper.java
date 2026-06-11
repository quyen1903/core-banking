package com.quinnbank.core.account.api.mapper;

import com.quinnbank.core.account.api.dto.AccountResponse;
import com.quinnbank.core.account.api.dto.OpenAccountRequest;
import com.quinnbank.core.account.application.command.OpenAccountCommand;
import com.quinnbank.core.account.application.result.AccountSnapshot;
import org.springframework.stereotype.Component;

@Component
public class AccountHttpMapper {

    public OpenAccountCommand toCommand(OpenAccountRequest request, String idempotencyKey) {
        return new OpenAccountCommand(
                request.customerId(),
                request.productCode(),
                idempotencyKey
        );
    }

    public AccountResponse toResponse(AccountSnapshot account) {
        return AccountResponse.from(account);
    }
}
