package com.quinnbank.core.employee.infrastructure.persistence;

import com.quinnbank.core.employee.domain.model.EmployeeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "employee")
class EmployeeJpaEntity {

    @Id
    private UUID id;

    @Column(name = "employee_number", nullable = false, unique = true, length = 50)
    private String employeeNumber;

    @Column(name = "identity_account_id")
    private UUID identityUserPublicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EmployeeStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    static EmployeeJpaEntity create(
            UUID id,
            String employeeNumber,
            UUID identityUserPublicId,
            EmployeeStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long version
    ) {
        EmployeeJpaEntity entity = new EmployeeJpaEntity();
        entity.id = id;
        entity.employeeNumber = employeeNumber;
        entity.identityUserPublicId = identityUserPublicId;
        entity.status = status;
        entity.createdAt = createdAt;
        entity.updatedAt = updatedAt;
        entity.version = version;
        return entity;
    }
}
