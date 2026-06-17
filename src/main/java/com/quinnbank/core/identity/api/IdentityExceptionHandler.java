package com.quinnbank.core.identity.api;

import com.quinnbank.core.common.api.ApiErrorResponse;
import com.quinnbank.core.identity.api.command.IdentityUserCommandController;
import com.quinnbank.core.identity.api.query.IdentityUserQueryController;
import com.quinnbank.core.identity.application.DuplicateIdentityEmailException;
import com.quinnbank.core.identity.application.DuplicateIdentityPhoneNumberException;
import com.quinnbank.core.identity.application.DuplicateIdentityUsernameException;
import com.quinnbank.core.identity.application.IdentityRoleNotFoundException;
import com.quinnbank.core.identity.application.IdentityUserNotFoundException;
import com.quinnbank.core.identity.domain.exception.IdentityCredentialRejectedException;
import com.quinnbank.core.identity.domain.exception.IdentityUserCreationRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {IdentityUserCommandController.class, IdentityUserQueryController.class})
public class IdentityExceptionHandler {

    @ExceptionHandler(DuplicateIdentityUsernameException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateUsername(DuplicateIdentityUsernameException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("IDENTITY_USERNAME_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateIdentityEmailException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateIdentityEmailException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("IDENTITY_EMAIL_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler(DuplicateIdentityPhoneNumberException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicatePhoneNumber(DuplicateIdentityPhoneNumberException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("IDENTITY_PHONE_NUMBER_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler(IdentityUserNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleUserNotFound(IdentityUserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("IDENTITY_USER_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(IdentityRoleNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleRoleNotFound(IdentityRoleNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("IDENTITY_ROLE_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(IdentityUserCreationRejectedException.class)
    ResponseEntity<ApiErrorResponse> handleCreationRejected(IdentityUserCreationRejectedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse("IDENTITY_USER_CREATION_REJECTED", exception.getMessage()));
    }

    @ExceptionHandler(IdentityCredentialRejectedException.class)
    ResponseEntity<ApiErrorResponse> handleCredentialRejected(IdentityCredentialRejectedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse("IDENTITY_CREDENTIAL_REJECTED", exception.getMessage()));
    }
}
