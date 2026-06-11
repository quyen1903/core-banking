package com.quinnbank.core.cif.api;

import com.quinnbank.core.common.api.ApiErrorResponse;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = CustomerController.class)
public class CifExceptionHandler {

    @ExceptionHandler(DuplicateCustomerEmailException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateEmail(DuplicateCustomerEmailException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("CUSTOMER_EMAIL_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleCustomerNotFound(CustomerNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("CUSTOMER_NOT_FOUND", exception.getMessage()));
    }

}
