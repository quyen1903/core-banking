package com.quinnbank.core.employee.api.dto;

import com.quinnbank.core.employee.application.result.EmployeeSnapshot;

import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String employeeNumber,
        UUID identityAccountId,
        String fullName,
        String workEmail,
        String jobTitle,
        String primaryBranchCode,
        String status
) {

    public static EmployeeResponse from(EmployeeSnapshot employee) {
        return new EmployeeResponse(
            employee.id(),
            employee.employeeNumber(),
            employee.identityAccountId(),
            employee.fullName(),
            employee.workEmail(),
            employee.jobTitle(),
            employee.primaryBranchCode(),
            employee.status().name()
        );
    }
}
