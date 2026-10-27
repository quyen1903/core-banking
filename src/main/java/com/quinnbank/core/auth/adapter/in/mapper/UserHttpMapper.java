package com.quinnbank.core.auth.adapter.in.mapper;

import com.quinnbank.core.auth.adapter.in.dto.request.RegisterUserRequest;
import com.quinnbank.core.auth.adapter.in.dto.response.RegisterIdentityUserResponse;
import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;


public final class UserHttpMapper {
    private UserHttpMapper() {}
    
    public static RegisterIdentityUserCommand toRegisterCommand(RegisterUserRequest request) {
        return new RegisterIdentityUserCommand(
            request.username(),
            request.password(),
            request.ownerType()
        );
    }

    public static RegisterIdentityUserResponse toRegisterResponse(RegisterIdentityUserResult result) {
        return new RegisterIdentityUserResponse(
            result.publicUserId(),
            result.status()
        );
    }
}