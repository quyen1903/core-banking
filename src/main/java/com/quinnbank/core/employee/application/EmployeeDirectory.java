package com.quinnbank.core.employee.application;

import com.quinnbank.core.employee.application.result.EmployeeSnapshot;

import java.util.UUID;

public interface EmployeeDirectory {

    EmployeeSnapshot requireActiveEmployee(UUID employeeId);
}
