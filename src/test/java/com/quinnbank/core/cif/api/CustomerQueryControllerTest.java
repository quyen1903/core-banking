package com.quinnbank.core.cif.api;

import com.quinnbank.core.cif.api.query.CustomerQueryController;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import com.quinnbank.core.cif.application.port.in.GetCustomerByIdUseCase;
import com.quinnbank.core.cif.application.query.GetCustomerByIdQuery;
import com.quinnbank.core.cif.application.result.GetCustomerByIdResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerQueryControllerTest {
    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000123");
    private static final String LOOKUP_PATH = "/api/v1/customers/{id}";

    private AnnotationConfigWebApplicationContext context;
    private GetCustomerByIdUseCase getCustomerByIdUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        context = createContext("local");
        getCustomerByIdUseCase = context.getBean(GetCustomerByIdUseCase.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void returnsOnlyCustomerResponseFieldsAndMapsIdToQuery() throws Exception {
        when(getCustomerByIdUseCase.getCustomerById(any())).thenReturn(new GetCustomerByIdResult(
                CUSTOMER_ID, "Synthetic", "Customer", "synthetic@example.invalid", "local-only",
                "Synthetic Customer"));

        mockMvc.perform(get(LOOKUP_PATH, CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(content().json("""
                        {
                          "id":"00000000-0000-0000-0000-000000000123",
                          "firstName":"Synthetic",
                          "lastName":"Customer",
                          "email":"synthetic@example.invalid",
                          "phoneNumber":"local-only",
                          "fullName":"Synthetic Customer"
                        }
                        """))
                .andExpect(jsonPath("$.length()").value(6));

        verify(getCustomerByIdUseCase).getCustomerById(new GetCustomerByIdQuery(CUSTOMER_ID));
    }

    @Test
    void preservesLegacyFullNameAndAbsentOptionalFields() throws Exception {
        when(getCustomerByIdUseCase.getCustomerById(any())).thenReturn(new GetCustomerByIdResult(
                CUSTOMER_ID, null, null, null, null, "Synthetic Legacy Customer"));

        mockMvc.perform(get(LOOKUP_PATH, CUSTOMER_ID))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(content().json("""
                        {
                          "id":"00000000-0000-0000-0000-000000000123",
                          "firstName":null,
                          "lastName":null,
                          "email":null,
                          "phoneNumber":null,
                          "fullName":"Synthetic Legacy Customer"
                        }
                        """))
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    void missingCustomerReturnsSafeNotFoundWithoutEchoingId() throws Exception {
        when(getCustomerByIdUseCase.getCustomerById(any()))
                .thenThrow(CustomerNotFoundException.byId(CUSTOMER_ID));

        mockMvc.perform(get(LOOKUP_PATH, CUSTOMER_ID))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(content().json("""
                        {"code":"CUSTOMER_NOT_FOUND","message":"Customer not found."}
                        """))
                .andExpect(jsonPath("$.length()").value(2));
    }

    @ParameterizedTest
    @ValueSource(strings = {"not-a-uuid", "12345", "synthetic-secret@example.invalid"})
    void malformedIdReturnsSafeBadRequestWithoutReadingCustomer(String id) throws Exception {
        mockMvc.perform(get(LOOKUP_PATH, id))
                .andExpect(status().isBadRequest())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(content().json("""
                        {"code":"INVALID_CUSTOMER_ID","message":"The customer ID must be a valid UUID."}
                        """))
                .andExpect(jsonPath("$.length()").value(2));

        verifyNoInteractions(getCustomerByIdUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "dev", "test", "qa", "uat", "staging", "pre-prod", "prod",
            "local,dev", "local,test", "local,qa", "local,uat", "local,staging", "local,pre-prod", "local,prod"})
    void lookupIsNotRegisteredOutsideIsolatedLocalProfile(String profiles) throws Exception {
        try (var restrictedContext = createContext(profiles.isEmpty() ? new String[0] : profiles.split(","))) {
            assertThat(restrictedContext.getBeansOfType(CustomerQueryController.class)).isEmpty();
            MockMvc restrictedMvc = MockMvcBuilders.webAppContextSetup(restrictedContext).build();

            restrictedMvc.perform(get(LOOKUP_PATH, CUSTOMER_ID)).andExpect(status().isNotFound());
            verifyNoInteractions(restrictedContext.getBean(GetCustomerByIdUseCase.class));
        }
    }

    @Test
    void localProfileLoadsLoopbackBindingFromTrackedApplicationConfiguration() {
        new ApplicationContextRunner()
                .withInitializer(new ConfigDataApplicationContextInitializer())
                .withPropertyValues("spring.profiles.active=local",
                        "spring.config.location=classpath:application.yml")
                .run(applicationContext -> {
                    assertThat(applicationContext).hasNotFailed();
                    assertThat(applicationContext.getEnvironment().getProperty("server.address"))
                            .isEqualTo("127.0.0.1");
                });
    }

    private static AnnotationConfigWebApplicationContext createContext(String... profiles) {
        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.setServletContext(new MockServletContext());
        applicationContext.getEnvironment().setActiveProfiles(profiles);
        applicationContext.register(WebConfiguration.class, CustomerQueryController.class, CifExceptionHandler.class);
        applicationContext.refresh();
        return applicationContext;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    static class WebConfiguration {
        @Bean
        GetCustomerByIdUseCase getCustomerByIdUseCase() {
            return mock(GetCustomerByIdUseCase.class);
        }
    }
}
