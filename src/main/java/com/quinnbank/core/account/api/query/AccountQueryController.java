package com.quinnbank.core.account.api.query;

import com.quinnbank.core.account.api.dto.AccountResponse;
import com.quinnbank.core.account.api.mapper.AccountHttpMapper;
import com.quinnbank.core.account.application.port.in.GetAccountUseCase;
import com.quinnbank.core.account.application.query.GetAccountByIdQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountQueryController {

    private final GetAccountUseCase getAccountUseCase;
    private final AccountHttpMapper mapper;

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAuthority('ACCOUNT_READ')")
    public AccountResponse getAccount(@PathVariable UUID accountId) {
        return mapper.toResponse(getAccountUseCase.getById(new GetAccountByIdQuery(accountId)));
    }
}
