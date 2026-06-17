package com.quinnbank.core.identity.api.command;

import com.quinnbank.core.identity.api.dto.CreateIdentityAccountRequest;
import com.quinnbank.core.identity.api.dto.IdentityAccountResponse;
import com.quinnbank.core.identity.api.mapper.IdentityAccountHttpMapper;
import com.quinnbank.core.identity.application.port.in.CreateIdentityAccountUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/accounts")
@RequiredArgsConstructor
public class IdentityAccountCommandController {

    private final CreateIdentityAccountUseCase createIdentityAccountUseCase;
    private final IdentityAccountHttpMapper mapper;

    @PostMapping
    @PreAuthorize("hasAuthority('IDENTITY_ACCOUNT_CREATE')")
    public IdentityAccountResponse createIdentityAccount(@Valid @RequestBody CreateIdentityAccountRequest request) {
        return mapper.toResponse(createIdentityAccountUseCase.create(mapper.toCommand(request)));
    }
}
