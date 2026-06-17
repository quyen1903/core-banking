package com.quinnbank.core.employee.infrastructure.persistence;

import com.quinnbank.core.employee.domain.model.Employee;
import com.quinnbank.core.employee.domain.model.EmployeeBranchAssignment;
import com.quinnbank.core.employee.domain.model.EmployeeProfile;

import java.util.UUID;

class EmployeePersistenceMapper {

    EmployeeJpaEntity toEmployeeEntity(Employee employee) {
        return EmployeeJpaEntity.create(
                employee.id(),
                employee.employeeNumber(),
                employee.identityUserPublicId(),
                employee.status(),
                employee.createdAt(),
                employee.updatedAt(),
                employee.version()
        );
    }

    EmployeeProfileJpaEntity toProfileEntity(Employee employee) {
        return EmployeeProfileJpaEntity.create(
                employee.id(),
                employee.profile().fullName(),
                employee.profile().workEmail(),
                employee.profile().jobTitle()
        );
    }

    EmployeeBranchAssignmentJpaEntity toBranchAssignmentEntity(Employee employee) {
        return EmployeeBranchAssignmentJpaEntity.create(
                UUID.randomUUID(),
                employee.id(),
                employee.primaryBranchAssignment().branchCode(),
                employee.primaryBranchAssignment().primaryAssignment(),
                true,
                employee.primaryBranchAssignment().assignedAt()
        );
    }

    Employee toDomain(
            EmployeeJpaEntity employee,
            EmployeeProfileJpaEntity profile,
            EmployeeBranchAssignmentJpaEntity branchAssignment
    ) {
        return Employee.restore(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getIdentityUserPublicId(),
                employee.getStatus(),
                new EmployeeProfile(profile.getFullName(), profile.getWorkEmail(), profile.getJobTitle()),
                new EmployeeBranchAssignment(
                        branchAssignment.getBranchCode(),
                        branchAssignment.isPrimaryAssignment(),
                        branchAssignment.getAssignedAt()
                ),
                employee.getCreatedAt(),
                employee.getUpdatedAt(),
                employee.getVersion()
        );
    }
}
