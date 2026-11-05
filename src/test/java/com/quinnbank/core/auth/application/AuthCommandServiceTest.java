package com.quinnbank.core.auth.application;

import com.quinnbank.core.auth.application.command.RegisterIdentityUserCommand;
import com.quinnbank.core.auth.application.port.out.IdentityCredentialWritePort;
import com.quinnbank.core.auth.application.port.out.IdentityUserWritePort;
import com.quinnbank.core.auth.application.port.out.PasswordHashingPort;
import com.quinnbank.core.auth.application.service.AuthCommandService;
import com.quinnbank.core.auth.domain.enums.IdentityOwnerType;
import com.quinnbank.core.auth.domain.model.IdentityCredential;
import com.quinnbank.core.auth.domain.model.IdentityUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AuthCommandServiceTest {
    /**
     * three mocked dependencies of the service under test.
     * Not connect to postgres, not connected to any other service, no real password hashing.
     * Only testing the logic of the service itself, not the dependencies.
     * This is the purpose of hexagon architecture, to isolate the service from its dependencies and test it in isolation.
     */
    private final IdentityUserWritePort users =mock(IdentityUserWritePort.class);
    private final IdentityCredentialWritePort credentials =mock(IdentityCredentialWritePort.class);
    private final PasswordHashingPort hashing =mock(PasswordHashingPort.class);

    //the real dependency
    private final AuthCommandService service = new AuthCommandService(users, credentials, hashing);


    /**
     * Test that the service correctly hashes the password and saves the user and credential.
     * The test uses a mocked hashing service that returns a placeholder string for the encoded password.
     */
    @Test
    void registrationStoresEncodedPasswordForTheCreatedUser() {
        // Arrange
        var command = command();

        // Mock the hashing service to return a placeholder string for the encoded password
        when(hashing.encode(command.password()))
            .thenReturn("encoded-placeholder-for-unit-test");

        // Act
        service.register(command);

        //get the object which was passed to save method.
        var userCaptor = ArgumentCaptor.forClass(IdentityUser.class);
        var credentialCaptor = ArgumentCaptor.forClass(IdentityCredential.class);

        verify(users).save(userCaptor.capture());
        verify(credentials).save(credentialCaptor.capture());

        IdentityUser user = userCaptor.getValue();
        IdentityCredential credential = credentialCaptor.getValue();

        //prove that credential is linked to the user and that the password is hashed.
        assertThat(user.getUsername()).isEqualTo("synthetic.user");
        assertThat(user.getOwnerId()).isEqualTo(command.ownerId());
        assertThat(credential.getUserId()).isEqualTo(user.getId());
        assertThat(credential.getPasswordHash())
            .isEqualTo("encoded-placeholder-for-unit-test")
            .isNotEqualTo(command.password());
    }

    /**
     * Test that the service does not hash or save anything when a user with a duplicate username is registered.
     */
    @Test
    void duplicateUsernameDoesNotHashOrSaveAnything() {
        // Arrange
        when(users.existsByUsernameNormalized("synthetic.user"))
            .thenReturn(true);

        // Act + Assert
        assertThatThrownBy(() -> service.register(command()))
            .isInstanceOf(IllegalArgumentException.class);

        verify(users, never()).save(any(IdentityUser.class));
        verifyNoInteractions(hashing, credentials);
    }

    private static RegisterIdentityUserCommand command() {
        return new RegisterIdentityUserCommand(
            " Synthetic.User ",
            "local-only-password",
            IdentityOwnerType.CUSTOMER,
            UUID.fromString("00000000-0000-0000-0000-000000000123")
        );
    }
}