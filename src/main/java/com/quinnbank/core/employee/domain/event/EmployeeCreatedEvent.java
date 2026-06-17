package com.quinnbank.core.employee.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeCreatedEvent(
        UUID employeeId,
        String employeeNumber,
        String primaryBranchCode,
        LocalDateTime occurredAt
) {
}
