package com.quinnbank.core.employee.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataEmployeeRepository extends JpaRepository<EmployeeJpaEntity, UUID> {

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<EmployeeJpaEntity> findByEmployeeNumber(String employeeNumber);
}
