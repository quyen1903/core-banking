package com.quinnbank.core.employee.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataEmployeeBranchAssignmentRepository extends JpaRepository<EmployeeBranchAssignmentJpaEntity, UUID> {

    Optional<EmployeeBranchAssignmentJpaEntity> findFirstByEmployeeIdAndActiveTrueOrderByPrimaryAssignmentDescAssignedAtDesc(UUID employeeId);
}
