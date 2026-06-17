package com.quinnbank.core.employee.api.command;

import com.quinnbank.core.employee.api.dto.CreateEmployeeRequest;
import com.quinnbank.core.employee.api.dto.EmployeeResponse;
import com.quinnbank.core.employee.api.mapper.EmployeeHttpMapper;
import com.quinnbank.core.employee.application.port.in.CreateEmployeeUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeCommandController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final EmployeeHttpMapper mapper;

    @PostMapping
   @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    public EmployeeResponse createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return mapper.toResponse(createEmployeeUseCase.create(mapper.toCommand(request)));
    }
}
