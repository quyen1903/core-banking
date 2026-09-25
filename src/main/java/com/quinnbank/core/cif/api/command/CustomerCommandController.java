package com.quinnbank.core.cif.api.command;

import com.quinnbank.core.cif.api.dto.request.RegisterCustomerRequest;
import com.quinnbank.core.cif.api.dto.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.api.mapper.CustomerHttpMapper;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerCommandController {

    private final RegisterCustomerUseCase registerCustomerUseCase;

    public CustomerCommandController(RegisterCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(
        @Valid
        @RequestBody 
        RegisterCustomerRequest request
    ) {
        var result = registerCustomerUseCase.registerCustomer(CustomerHttpMapper.toCommand(request));
        return ResponseEntity.ok(CustomerHttpMapper.toResponse(result));
    }
}
