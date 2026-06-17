package com.quinnbank.core.employee.api;

import com.quinnbank.core.common.api.ApiErrorResponse;
import com.quinnbank.core.employee.api.command.EmployeeCommandController;
import com.quinnbank.core.employee.application.DuplicateEmployeeWorkEmailException;
import com.quinnbank.core.employee.application.EmployeeNotFoundException;
import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = EmployeeCommandController.class)
public class EmployeeExceptionHandler {

    @ExceptionHandler(DuplicateEmployeeWorkEmailException.class)
    ResponseEntity<ApiErrorResponse> handleDuplicateWorkEmail(DuplicateEmployeeWorkEmailException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse("EMPLOYEE_WORK_EMAIL_ALREADY_EXISTS", exception.getMessage()));
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    ResponseEntity<ApiErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("EMPLOYEE_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(EmployeeCreationRejectedException.class)
    ResponseEntity<ApiErrorResponse> handleEmployeeCreationRejected(EmployeeCreationRejectedException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse("EMPLOYEE_CREATION_REJECTED", exception.getMessage()));
    }
}
