package com.quinnbank.core.identity.api;

import com.quinnbank.core.common.api.ApiErrorResponse;
import com.quinnbank.core.employee.application.EmployeeNotFoundException;
import com.quinnbank.core.identity.api.command.IdentityAccountCommandController;
import com.quinnbank.core.identity.application.DuplicateIdentityEmailException;
import com.quinnbank.core.identity.application.DuplicateIdentityUsernameException;
import com.quinnbank.core.identity.domain.exception.IdentityAccountCreationRejectedException;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = IdentityAccountCommandController.class)
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

    @ExceptionHandler({CustomerNotFoundException.class, EmployeeNotFoundException.class})
    ResponseEntity<ApiErrorResponse> handleSubjectNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("IDENTITY_SUBJECT_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(IdentityAccountCreationRejectedException.class)
    ResponseEntity<ApiErrorResponse> handleCreationRejected(IdentityAccountCreationRejectedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse("IDENTITY_ACCOUNT_CREATION_REJECTED", exception.getMessage()));
    }
}
