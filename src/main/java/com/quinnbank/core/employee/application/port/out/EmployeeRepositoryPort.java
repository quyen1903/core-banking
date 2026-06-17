package com.quinnbank.core.employee.application.port.out;

import com.quinnbank.core.employee.domain.model.Employee;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepositoryPort {

    Employee save(Employee employee);

    Optional<Employee> findById(UUID employeeId);

    boolean existsByEmployeeNumber(String employeeNumber);

    boolean existsByWorkEmail(String workEmail);
}
