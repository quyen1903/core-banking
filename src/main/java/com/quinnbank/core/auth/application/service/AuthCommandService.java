package com.quinnbank.core.auth.application.service;

import java.time.Instant;

import com.quinnbank.core.auth.application.command.RegisterCommand;
import com.quinnbank.core.auth.application.port.out.AuthWritePort;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.application.result.RegisterResult;
import com.quinnbank.core.auth.domain.model.Auth;
import com.quinnbank.core.auth.domain.model.PasswordCredential;

/**
 * 
 * AuthCommandService
 * 1Verify the registration request and identify the customer permitted to link
 * 1 Normalize and validate username
 * 2 Check if username is already in use
 * 3 Call Auth.create(...) to create identity
 * 4 Hash password using password encoder
 * 5 Create credential containing password hash
 * 6 Save identity, credential and audit in the same transaction
 * 7 Return safe result to client
 */
public class AuthCommandService {
    AuthWritePort authWritePort;
    PasswordHashingPort passwordHashingPort;

    AuthCommandService(
        AuthWritePort authWritePort, 
        PasswordHashingPort passwordHashingPort
    ) {
        this.authWritePort = authWritePort;
        this.passwordHashingPort = passwordHashingPort;
    }

    public RegisterResult register(RegisterCommand command) {
        // Registration logic here
        //1 Normalize and validate username
        String normalizedUsername = Auth.normalizeUsername(command.username());

        //2 Check if username is already in use
        if (normalizedUsername == null || authWritePort.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username is required");
        }

        //3 Call Auth.create(...) to create identity
        Auth auth = Auth.create(normalizedUsername, command.ownerType());

        //4 Hash password using password encoder
        String passwordHash = passwordHashingPort.encode(command.password());

        //5 Create credential containing password hash
        PasswordCredential credential = PasswordCredential.create(auth.getId(), passwordHash, Instant.now(), false);

        
        return new RegisterResult();
    }
}
