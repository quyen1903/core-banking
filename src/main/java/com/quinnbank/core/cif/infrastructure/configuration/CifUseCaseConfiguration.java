package com.quinnbank.core.cif.infrastructure.configuration;

import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
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
    RegisterCustomerUseCase registerCustomerUseCase(
            CustomerWritePort customers, CustomerNumberGeneratorPort customerNumbers, Clock clock) {
        return new TransactionalRegisterCustomerUseCase(
                new CustomerCommandService(customers, customerNumbers, clock));
    }

    @Bean
    GetCustomerByIdUseCase getCustomerByIdUseCase(CustomerReadPort customers) {
        return new TransactionalGetCustomerByIdUseCase(new CustomerQueryService(customers));
    }
}
