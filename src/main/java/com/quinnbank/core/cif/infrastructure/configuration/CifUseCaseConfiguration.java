package com.quinnbank.core.cif.infrastructure.configuration;

import com.quinnbank.core.cif.application.port.in.QueryCustomerUseCase;
import com.quinnbank.core.cif.application.port.in.CommandCustomerUseCase;
import com.quinnbank.core.cif.application.port.out.CustomerNumberGeneratorPort;
import com.quinnbank.core.cif.application.port.out.CustomerReadPort;
import com.quinnbank.core.cif.application.port.out.CustomerWritePort;
import com.quinnbank.core.cif.application.service.CustomerCommandService;
import com.quinnbank.core.cif.application.service.CustomerQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
public class CifUseCaseConfiguration {
    @Bean
    CommandCustomerUseCase commandCustomerUseCase(
        CustomerWritePort customers, 
        CustomerNumberGeneratorPort customerNumbers, 
        Clock clock
    ) {
        return new TransactionalCommandCustomerUseCase(new CustomerCommandService(customers, customerNumbers, clock));
    }

    @Bean
    QueryCustomerUseCase queryCustomerUseCase(CustomerReadPort customers) {
        return new TransactionalGetCustomerByIdUseCase(new CustomerQueryService(customers));
    }
}
