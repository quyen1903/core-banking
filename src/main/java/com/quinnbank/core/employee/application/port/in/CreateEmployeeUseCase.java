package com.quinnbank.core.employee.application.port.in;

import com.quinnbank.core.employee.application.command.CreateEmployeeCommand;
import com.quinnbank.core.employee.application.result.EmployeeSnapshot;

public interface CreateEmployeeUseCase {

    EmployeeSnapshot create(CreateEmployeeCommand command);
}
