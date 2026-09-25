package com.quinnbank.core.cif.api.query;

import com.quinnbank.core.cif.api.dto.response.GetCustomerByIdResponse;
import com.quinnbank.core.cif.api.mapper.CustomerHttpMapper;
import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import org.springframework.context.annotation.Profile;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Local synthetic-data lookup until authenticated, resource-scoped access is implemented. */
@RestController
// @Profile("local & !dev & !test & !qa & !uat & !staging & !pre-prod & !prod")
@RequestMapping("/api/v1/customers")
public class CustomerQueryController {
    private final GetCustomerByIdUseCase getCustomerByIdUseCase;

    public CustomerQueryController(GetCustomerByIdUseCase getCustomerByIdUseCase) {
        this.getCustomerByIdUseCase = getCustomerByIdUseCase;
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetCustomerByIdResponse> getCustomerById(@PathVariable("id") UUID id) {
        var result = getCustomerByIdUseCase.getCustomerById(CustomerHttpMapper.toQuery(id));
        
        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noStore())
            .body(CustomerHttpMapper.toResponse(result));
    }
}
