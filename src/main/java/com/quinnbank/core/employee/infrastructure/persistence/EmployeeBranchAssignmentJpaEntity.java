package com.quinnbank.core.employee.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "employee_branch_assignments")
class EmployeeBranchAssignmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "branch_code", nullable = false, length = 50)
    private String branchCode;

    @Column(name = "primary_assignment", nullable = false)
    private boolean primaryAssignment;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    static EmployeeBranchAssignmentJpaEntity create(
            UUID id,
            UUID employeeId,
            String branchCode,
            boolean primaryAssignment,
            boolean active,
            LocalDateTime assignedAt
    ) {
        EmployeeBranchAssignmentJpaEntity entity = new EmployeeBranchAssignmentJpaEntity();
        entity.id = id;
        entity.employeeId = employeeId;
        entity.branchCode = branchCode;
        entity.primaryAssignment = primaryAssignment;
        entity.active = active;
        entity.assignedAt = assignedAt;
        return entity;
    }
}
