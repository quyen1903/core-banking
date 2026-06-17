package com.quinnbank.core.identity.api.mapper;

import com.quinnbank.core.identity.api.dto.CreateIdentityAccountRequest;
import com.quinnbank.core.identity.api.dto.IdentityAccountResponse;
import com.quinnbank.core.identity.application.command.CreateIdentityAccountCommand;
import com.quinnbank.core.identity.application.result.IdentityAccountSnapshot;
import org.springframework.stereotype.Component;

@Component
public class IdentityAccountHttpMapper {

    public CreateIdentityAccountCommand toCommand(CreateIdentityAccountRequest request) {
        return new CreateIdentityAccountCommand(
                request.username(),
                request.email(),
                request.subjectType(),
                request.subjectId(),
                request.initialPassword()
        );
    }

    public IdentityAccountResponse toResponse(IdentityAccountSnapshot identityAccount) {
        return IdentityAccountResponse.from(identityAccount);
    }
}
