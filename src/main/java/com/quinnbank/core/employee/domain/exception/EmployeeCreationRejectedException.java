package com.quinnbank.core.employee.domain.exception;

public class EmployeeCreationRejectedException extends RuntimeException {

    public EmployeeCreationRejectedException(String message) {
        super(message);
    }
}
