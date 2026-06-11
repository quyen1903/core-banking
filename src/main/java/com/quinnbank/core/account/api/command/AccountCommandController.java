package com.quinnbank.core.account.api.command;

import com.quinnbank.core.account.api.dto.AccountResponse;
import com.quinnbank.core.account.api.dto.OpenAccountRequest;
import com.quinnbank.core.account.api.mapper.AccountHttpMapper;
import com.quinnbank.core.account.application.port.in.OpenAccountUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountCommandController {

    private final OpenAccountUseCase openAccountUseCase;
    private final AccountHttpMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ACCOUNT_OPEN')")
    public AccountResponse openAccount(
            @RequestHeader("Idempotency-Key") @NotBlank @Size(max = 120) String idempotencyKey,
            @Valid @RequestBody OpenAccountRequest request
    ) {
        return mapper.toResponse(openAccountUseCase.open(mapper.toCommand(request, idempotencyKey)));
    }
}
