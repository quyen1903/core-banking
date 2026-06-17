package com.quinnbank.core.employee.application.service;

import com.quinnbank.core.employee.application.DuplicateEmployeeWorkEmailException;
import com.quinnbank.core.employee.application.command.CreateEmployeeCommand;
import com.quinnbank.core.employee.application.port.in.CreateEmployeeUseCase;
import com.quinnbank.core.employee.application.port.out.EmployeeNumberGeneratorPort;
import com.quinnbank.core.employee.application.port.out.EmployeeRepositoryPort;
import com.quinnbank.core.employee.application.result.EmployeeSnapshot;
import com.quinnbank.core.employee.domain.exception.EmployeeCreationRejectedException;
import com.quinnbank.core.employee.domain.model.Employee;
import com.quinnbank.core.employee.domain.model.EmployeeBranchAssignment;
import com.quinnbank.core.employee.domain.model.EmployeeProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateEmployeeService implements CreateEmployeeUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final EmployeeNumberGeneratorPort employeeNumberGenerator;
    private final Clock clock;

    @Override
    @Transactional
    public EmployeeSnapshot create(CreateEmployeeCommand command) {
        validate(command);

        String normalizedWorkEmail = EmployeeProfile.normalizeEmail(command.workEmail());
        if (employeeRepository.existsByWorkEmail(normalizedWorkEmail)) {
            throw new DuplicateEmployeeWorkEmailException("employee work email already exists");
        }

        LocalDateTime createdAt = LocalDateTime.now(clock);
        Employee employee = Employee.create(
                employeeNumberGenerator.nextEmployeeNumber(),
                new EmployeeProfile(command.fullName(), normalizedWorkEmail, command.jobTitle()),
                new EmployeeBranchAssignment(command.branchCode(), true, createdAt),
                createdAt
        );

        return EmployeeSnapshot.from(employeeRepository.save(employee));
    }

    private static void validate(CreateEmployeeCommand command) {
        if (command == null) {
            throw new EmployeeCreationRejectedException("create employee command is required");
        }
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new EmployeeCreationRejectedException("employee full name is required");
        }
        if (command.workEmail() == null || command.workEmail().isBlank()) {
            throw new EmployeeCreationRejectedException("employee work email is required");
        }
        if (command.branchCode() == null || command.branchCode().isBlank()) {
            throw new EmployeeCreationRejectedException("employee branch code is required");
        }
    }
}
