package com.quinnbank.core.auth.adapter.in.command;

import com.quinnbank.core.auth.adapter.in.dto.request.RegisterUserRequest;
import com.quinnbank.core.auth.adapter.in.dto.response.RegisterIdentityUserResponse;
import com.quinnbank.core.auth.adapter.in.mapper.UserHttpMapper;
import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;
import com.quinnbank.core.auth.application.result.RegisterIdentityUserResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthCommandController {
    CommandAuthUseCase command;

    AuthCommandController(CommandAuthUseCase command) {
        this.command = command;
    }

    @PostMapping(
        path = "/register",
        consumes = "application/json"
    )
    public ResponseEntity<RegisterIdentityUserResponse> registerUser(RegisterUserRequest request) {
        RegisterIdentityUserResult result = command.register(UserHttpMapper.toRegisterCommand(request));
        return ResponseEntity.ok(UserHttpMapper.toRegisterResponse(result));
    }
}
