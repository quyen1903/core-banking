package com.quinnbank.core.cif.api.command;

import com.quinnbank.core.cif.api.dto.request.RegisterCustomerRequest;
import com.quinnbank.core.cif.api.dto.request.UpdateCustomerRequest;
import com.quinnbank.core.cif.api.dto.response.RegisterCustomerResponse;
import com.quinnbank.core.cif.api.dto.response.UpdateCustomerByIdResponse;
import com.quinnbank.core.cif.api.mapper.CustomerHttpMapper;
import com.quinnbank.core.cif.application.port.in.CommandCustomerUseCase;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.application.result.UpdateCustomerResult;

import jakarta.validation.Valid;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerCommandController {

    private final CommandCustomerUseCase registerCustomerUseCase;

    public CustomerCommandController(CommandCustomerUseCase registerCustomerUseCase) {
        this.registerCustomerUseCase = registerCustomerUseCase;
    }

    @PostMapping(
        path = "/register", 
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(
        @Valid
        @RequestBody 
        RegisterCustomerRequest request
    ) {
        RegisterCustomerResult result = registerCustomerUseCase.registerCustomer(CustomerHttpMapper.toRegisterCommand(request));
        return ResponseEntity.ok(CustomerHttpMapper.RegisterCustomerResponse(result));
    }

    @PatchMapping(
        path = "/{id}", 
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UpdateCustomerByIdResponse> updateCustomer(
        @PathVariable("id") String id,
        @RequestBody UpdateCustomerRequest request
    ) {
        UpdateCustomerResult result = registerCustomerUseCase.updateCustomer(CustomerHttpMapper.toUpdateCommand(UUID.fromString(id), request));
        return ResponseEntity.ok(CustomerHttpMapper.UpdateCustomerByIdResponse(result));
    }
}
