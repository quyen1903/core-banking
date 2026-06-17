package com.quinnbank.core.employee.api.mapper;

import com.quinnbank.core.employee.api.dto.CreateEmployeeRequest;
import com.quinnbank.core.employee.api.dto.EmployeeResponse;
import com.quinnbank.core.employee.application.command.CreateEmployeeCommand;
import com.quinnbank.core.employee.application.result.EmployeeSnapshot;
import org.springframework.stereotype.Component;

@Component
public class EmployeeHttpMapper {

    public CreateEmployeeCommand toCommand(CreateEmployeeRequest request) {
        return new CreateEmployeeCommand(
                request.fullName(),
                request.workEmail(),
                request.jobTitle(),
                request.branchCode()
        );
    }

    public EmployeeResponse toResponse(EmployeeSnapshot employee) {
        return EmployeeResponse.from(employee);
    }
}
