package com.quinnbank.core.employee.application;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {

    private EmployeeNotFoundException(String message) {
        super(message);
    }

    public static EmployeeNotFoundException byId(UUID employeeId) {
        return new EmployeeNotFoundException("employee not found");
    }

    public static EmployeeNotFoundException activeEmployeeRequired(UUID employeeId) {
        return new EmployeeNotFoundException("active employee required");
    }
}
