package com.quinnbank.core.employee.infrastructure.persistence;

import com.quinnbank.core.employee.application.port.out.EmployeeRepositoryPort;
import com.quinnbank.core.employee.domain.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class EmployeePersistenceAdapter implements EmployeeRepositoryPort {

    private final SpringDataEmployeeRepository employeeRepository;
    private final SpringDataEmployeeProfileRepository profileRepository;
    private final SpringDataEmployeeBranchAssignmentRepository branchAssignmentRepository;
    private final EmployeePersistenceMapper mapper = new EmployeePersistenceMapper();

    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity savedEmployee = employeeRepository.save(mapper.toEmployeeEntity(employee));
        EmployeeProfileJpaEntity savedProfile = profileRepository.save(mapper.toProfileEntity(employee));
        EmployeeBranchAssignmentJpaEntity savedBranchAssignment = branchAssignmentRepository.save(
                mapper.toBranchAssignmentEntity(employee)
        );
        return mapper.toDomain(savedEmployee, savedProfile, savedBranchAssignment);
    }

    @Override
    public Optional<Employee> findById(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .flatMap(employee -> profileRepository.findById(employee.getId())
                        .flatMap(profile -> branchAssignmentRepository
                                .findFirstByEmployeeIdAndActiveTrueOrderByPrimaryAssignmentDescAssignedAtDesc(employee.getId())
                                .map(branchAssignment -> mapper.toDomain(employee, profile, branchAssignment))));
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        return employeeRepository.existsByEmployeeNumber(employeeNumber);
    }

    @Override
    public boolean existsByWorkEmail(String workEmail) {
        return profileRepository.existsByWorkEmail(workEmail);
    }
}
