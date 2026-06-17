package com.quinnbank.core.employee.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface SpringDataEmployeeProfileRepository extends JpaRepository<EmployeeProfileJpaEntity, UUID> {

    boolean existsByWorkEmail(String workEmail);
}
