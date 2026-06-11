package com.quinnbank.core.account.api;

import com.quinnbank.core.account.api.command.AccountCommandController;
import com.quinnbank.core.account.api.query.AccountQueryController;
import com.quinnbank.core.account.application.exception.AccountNotFoundException;
import com.quinnbank.core.account.application.exception.AccountOpeningConflictException;
import com.quinnbank.core.account.application.exception.AccountProductNotFoundException;
import com.quinnbank.core.common.api.ApiErrorResponse;
import com.quinnbank.core.account.domain.exception.AccountOpeningRejectedException;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {AccountCommandController.class, AccountQueryController.class})
public class AccountExceptionHandler {

    @ExceptionHandler(AccountOpeningConflictException.class)
    ResponseEntity<ApiErrorResponse> handleOpeningConflict(AccountOpeningConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("ACCOUNT_OPENING_IDEMPOTENCY_CONFLICT", exception.getMessage()));
    }

    @ExceptionHandler(AccountProductNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleProductNotFound(AccountProductNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ACCOUNT_PRODUCT_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleCustomerNotFound(CustomerNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ACTIVE_CUSTOMER_REQUIRED", exception.getMessage()));
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleAccountNotFound(AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("ACCOUNT_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(AccountOpeningRejectedException.class)
    ResponseEntity<ApiErrorResponse> handleOpeningRejected(AccountOpeningRejectedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse("ACCOUNT_OPENING_REJECTED", exception.getMessage()));
    }

}
