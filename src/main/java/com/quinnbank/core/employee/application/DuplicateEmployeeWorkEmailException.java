package com.quinnbank.core.employee.application;

public class DuplicateEmployeeWorkEmailException extends RuntimeException {

    public DuplicateEmployeeWorkEmailException(String message) {
        super(message);
    }
}
