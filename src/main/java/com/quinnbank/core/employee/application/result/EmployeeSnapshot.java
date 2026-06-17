package com.quinnbank.core.employee.application.result;

import com.quinnbank.core.employee.domain.model.Employee;
import com.quinnbank.core.employee.domain.model.EmployeeStatus;

import java.util.UUID;

public record EmployeeSnapshot(
        UUID id,
        String employeeNumber,
        UUID identityAccountId,
        String fullName,
        String workEmail,
        String jobTitle,
        String primaryBranchCode,
        EmployeeStatus status
) {

    public static EmployeeSnapshot from(Employee employee) {
        return new EmployeeSnapshot(
                employee.id(),
                employee.employeeNumber(),
                employee.identityAccountId(),
                employee.profile().fullName(),
                employee.profile().workEmail(),
                employee.profile().jobTitle(),
                employee.primaryBranchAssignment().branchCode(),
                employee.status()
        );
    }
}
