package com.quinnbank.core.employee.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "employee_profiles")
class EmployeeProfileJpaEntity {

    @Id
    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "work_email", nullable = false, unique = true, length = 255)
    private String workEmail;

    @Column(name = "job_title", length = 120)
    private String jobTitle;

    static EmployeeProfileJpaEntity create(UUID employeeId, String fullName, String workEmail, String jobTitle) {
        EmployeeProfileJpaEntity entity = new EmployeeProfileJpaEntity();
        entity.employeeId = employeeId;
        entity.fullName = fullName;
        entity.workEmail = workEmail;
        entity.jobTitle = jobTitle;
        return entity;
    }
}
