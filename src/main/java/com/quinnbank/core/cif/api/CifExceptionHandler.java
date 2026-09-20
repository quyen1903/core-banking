package com.quinnbank.core.cif.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.quinnbank.core.cif.api.command.CustomerCommandController;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;
import com.quinnbank.core.common.api.ApiErrorResponse;

@RestControllerAdvice(assignableTypes = CustomerCommandController.class)
public class CifExceptionHandler {

    @ExceptionHandler(DuplicateCustomerEmailException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateEmail() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse("CUSTOMER_EMAIL_ALREADY_EXISTS", "A customer with this email already exists."));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleCustomerNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiErrorResponse("CUSTOMER_NOT_FOUND", "Customer not found."));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            CustomerRegistrationRejectedException.class
    })
    ResponseEntity<ApiErrorResponse> handleInvalidRequest() {
        return ResponseEntity.badRequest()
            .body(new ApiErrorResponse("INVALID_CUSTOMER_REQUEST", "The customer registration request is invalid."));
    }
}
