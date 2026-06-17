package com.quinnbank.core.employee.domain;

import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;
import com.quinnbank.core.employee.domain.model.Employee;
import com.quinnbank.core.employee.domain.model.EmployeeBranchAssignment;
import com.quinnbank.core.employee.domain.model.EmployeeProfile;
import com.quinnbank.core.employee.domain.model.EmployeeStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeTest {

    @Test
    void createBuildsActiveEmployeeSeparateFromIdentityAndCustomer() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 16, 9, 0);

        Employee employee = Employee.create(
                " emp202606160000000001 ",
                new EmployeeProfile("  Branch Operator  ", "  OPERATOR@example.invalid  ", " Teller "),
                new EmployeeBranchAssignment(" hcm-001 ", true, createdAt),
                createdAt
        );

        assertThat(employee.id()).isNotNull();
        assertThat(employee.employeeNumber()).isEqualTo("EMP202606160000000001");
        assertThat(employee.identityAccountId()).isNull();
        assertThat(employee.profile().fullName()).isEqualTo("Branch Operator");
        assertThat(employee.profile().workEmail()).isEqualTo("operator@example.invalid");
        assertThat(employee.primaryBranchAssignment().branchCode()).isEqualTo("HCM-001");
        assertThat(employee.status()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(employee.pullDomainEvents()).hasSize(1);
    }

    @Test
    void createRejectsMissingBranchAssignment() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 16, 9, 0);

        assertThatThrownBy(() -> Employee.create(
                "EMP202606160000000001",
                new EmployeeProfile("Branch Operator", "operator@example.invalid", null),
                null,
                createdAt
        ))
                .isInstanceOf(EmployeeCreationRejectedException.class)
                .hasMessage("employee branch assignment is required");
    }
}
