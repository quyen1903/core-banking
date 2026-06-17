package com.quinnbank.core.employee.domain.model;

import com.quinnbank.core.employee.domain.event.EmployeeCreatedEvent;
import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Employee {

    private final UUID id;
    private final String employeeNumber;
    private final UUID identityAccountId;
    private final EmployeeStatus status;
    private final EmployeeProfile profile;
    private final EmployeeBranchAssignment primaryBranchAssignment;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final long version;
    private final List<Object> domainEvents = new ArrayList<>();

    private Employee(
            UUID id,
            String employeeNumber,
            UUID identityAccountId,
            EmployeeStatus status,
            EmployeeProfile profile,
            EmployeeBranchAssignment primaryBranchAssignment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        this.id = id;
        this.employeeNumber = employeeNumber;
        this.identityAccountId = identityAccountId;
        this.status = status;
        this.profile = profile;
        this.primaryBranchAssignment = primaryBranchAssignment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public static Employee create(
            String employeeNumber,
            EmployeeProfile profile,
            EmployeeBranchAssignment primaryBranchAssignment,
            LocalDateTime createdAt
    ) {
        requireEmployeeNumber(employeeNumber);
        if (profile == null) {
            throw new EmployeeCreationRejectedException("employee profile is required");
        }
        if (primaryBranchAssignment == null) {
            throw new EmployeeCreationRejectedException("employee branch assignment is required");
        }
        if (createdAt == null) {
            throw new EmployeeCreationRejectedException("employee creation time is required");
        }

        Employee employee = new Employee(
                UUID.randomUUID(),
                employeeNumber.trim().toUpperCase(),
                null,
                EmployeeStatus.ACTIVE,
                profile,
                primaryBranchAssignment,
                createdAt,
                createdAt,
                0
        );
        employee.domainEvents.add(new EmployeeCreatedEvent(
                employee.id,
                employee.employeeNumber,
                employee.primaryBranchAssignment.branchCode(),
                createdAt
        ));

        return employee;
    }

    public static Employee restore(
            UUID id,
            String employeeNumber,
            UUID identityAccountId,
            EmployeeStatus status,
            EmployeeProfile profile,
            EmployeeBranchAssignment primaryBranchAssignment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        if (id == null) {
            throw new IllegalArgumentException("employee id is required");
        }
        requireEmployeeNumber(employeeNumber);
        if (status == null) {
            throw new IllegalArgumentException("employee status is required");
        }
        if (profile == null) {
            throw new IllegalArgumentException("employee profile is required");
        }
        if (primaryBranchAssignment == null) {
            throw new IllegalArgumentException("employee branch assignment is required");
        }
        if (createdAt == null || updatedAt == null) {
            throw new IllegalArgumentException("employee timestamps are required");
        }

        return new Employee(
                id,
                employeeNumber.trim().toUpperCase(),
                identityAccountId,
                status,
                profile,
                primaryBranchAssignment,
                createdAt,
                updatedAt,
                version
        );
    }

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE;
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public UUID id() {
        return id;
    }

    public String employeeNumber() {
        return employeeNumber;
    }

    public UUID identityAccountId() {
        return identityAccountId;
    }

    public EmployeeStatus status() {
        return status;
    }

    public EmployeeProfile profile() {
        return profile;
    }

    public EmployeeBranchAssignment primaryBranchAssignment() {
        return primaryBranchAssignment;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public long version() {
        return version;
    }

    private static void requireEmployeeNumber(String employeeNumber) {
        if (employeeNumber == null || employeeNumber.isBlank()) {
            throw new EmployeeCreationRejectedException("employee number is required");
        }
    }
}
