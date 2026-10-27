package com.quinnbank.core.auth.adapter.in.command;

import com.quinnbank.core.auth.application.port.in.CommandAuthUseCase;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthCommandController {
    CommandAuthUseCase authCommandUseCase;

    AuthCommandController(CommandAuthUseCase authCommandUseCase) {
        this.authCommandUseCase = authCommandUseCase;
    }

    @PostMapping(
        path = "/users",
        consumes = "application/json"
    )
    public RegisterIdentityUserResult registerUser() {
        // Implementation for registering an identity user
    }
}
