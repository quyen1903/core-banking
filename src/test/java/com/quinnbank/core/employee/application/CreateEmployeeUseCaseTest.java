package com.quinnbank.core.employee.application;

import com.quinnbank.core.employee.application.command.CreateEmployeeCommand;
import com.quinnbank.core.employee.application.port.out.EmployeeNumberGeneratorPort;
import com.quinnbank.core.employee.application.port.out.EmployeeRepositoryPort;
import com.quinnbank.core.employee.application.result.EmployeeSnapshot;
import com.quinnbank.core.employee.application.service.CreateEmployeeService;
import com.quinnbank.core.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateEmployeeUseCaseTest {

    private final EmployeeRepositoryPort employeeRepository = mock(EmployeeRepositoryPort.class);
    private final EmployeeNumberGeneratorPort employeeNumberGenerator = mock(EmployeeNumberGeneratorPort.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-16T02:00:00Z"), ZoneOffset.UTC);
    private final CreateEmployeeService useCase = new CreateEmployeeService(
            employeeRepository,
            employeeNumberGenerator,
            clock
    );

    @Test
    void createStoresEmployeeWithoutIdentityOrCustomerData() {
        when(employeeRepository.existsByWorkEmail("operator@example.invalid")).thenReturn(false);
        when(employeeNumberGenerator.nextEmployeeNumber()).thenReturn("EMP202606160000000001");
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeSnapshot employee = useCase.create(new CreateEmployeeCommand(
                "Branch Operator",
                "OPERATOR@example.invalid",
                "Teller",
                "hcm-001"
        ));

        assertThat(employee.employeeNumber()).isEqualTo("EMP202606160000000001");
        assertThat(employee.identityAccountId()).isNull();
        assertThat(employee.workEmail()).isEqualTo("operator@example.invalid");
        assertThat(employee.primaryBranchCode()).isEqualTo("HCM-001");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createRejectsDuplicateWorkEmailBeforeGeneratingEmployeeNumber() {
        when(employeeRepository.existsByWorkEmail("operator@example.invalid")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(new CreateEmployeeCommand(
                "Branch Operator",
                "operator@example.invalid",
                "Teller",
                "hcm-001"
        )))
                .isInstanceOf(DuplicateEmployeeWorkEmailException.class)
                .hasMessage("employee work email already exists");

        verify(employeeNumberGenerator, never()).nextEmployeeNumber();
        verify(employeeRepository, never()).save(any(Employee.class));
    }
}
