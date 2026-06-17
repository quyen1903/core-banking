package com.quinnbank.core.employee.application.command;

public record CreateEmployeeCommand(
        String fullName,
        String workEmail,
        String jobTitle,
        String branchCode
) {
}
