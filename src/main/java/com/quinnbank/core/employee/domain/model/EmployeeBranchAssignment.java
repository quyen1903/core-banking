package com.quinnbank.core.employee.domain.model;

import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;

import java.time.LocalDateTime;

public record EmployeeBranchAssignment(
        String branchCode,
        boolean primaryAssignment,
        LocalDateTime assignedAt
) {

    public EmployeeBranchAssignment {
        if (branchCode == null || branchCode.isBlank()) {
            throw new EmployeeCreationRejectedException("employee branch code is required");
        }
        if (assignedAt == null) {
            throw new EmployeeCreationRejectedException("employee branch assignment time is required");
        }

        branchCode = branchCode.trim().toUpperCase();
    }
}
