package com.quinnbank.core.employee.application.service;

import com.quinnbank.core.employee.application.EmployeeDirectory;
import com.quinnbank.core.employee.application.EmployeeNotFoundException;
import com.quinnbank.core.employee.application.port.out.EmployeeRepositoryPort;
import com.quinnbank.core.employee.application.result.EmployeeSnapshot;
import com.quinnbank.core.employee.domain.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class EmployeeDirectoryService implements EmployeeDirectory {

    private final EmployeeRepositoryPort employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public EmployeeSnapshot requireActiveEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> EmployeeNotFoundException.byId(employeeId));

        if (!employee.isActive()) {
            throw EmployeeNotFoundException.activeEmployeeRequired(employeeId);
        }

        return EmployeeSnapshot.from(employee);
    }
}
