package com.quinnbank.core.cif.infrastructure.configuration;

import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalGetCustomerByIdUseCase implements GetCustomerByIdUseCase {
    private final GetCustomerByIdUseCase delegate;

    public TransactionalGetCustomerByIdUseCase(GetCustomerByIdUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCustomerByIdResult getCustomerById(GetCustomerByIdQuery query) {
        return delegate.getCustomerById(query);
    }
}
