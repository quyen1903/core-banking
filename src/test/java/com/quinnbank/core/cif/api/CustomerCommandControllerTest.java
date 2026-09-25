package com.quinnbank.core.cif.api;

import com.quinnbank.core.cif.api.command.CustomerCommandController;
import com.quinnbank.core.cif.application.CustomerNotFoundException;
import com.quinnbank.core.cif.application.DuplicateCustomerEmailException;
import com.quinnbank.core.cif.application.command.RegisterCustomerCommand;
import com.quinnbank.core.cif.application.port.in.RegisterCustomerUseCase;
import com.quinnbank.core.cif.application.result.RegisterCustomerResult;
import com.quinnbank.core.cif.domain.exception.CustomerRegistrationRejectedException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CustomerCommandControllerTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000123");
    private static final String REGISTRATION_PATH = "/api/v1/customers/register";
    private static final String VALID_PAYLOAD = """
            {
              "firstName": "Synthetic",
              "lastName": "Customer",
              "email": "synthetic@example.invalid",
              "phone": "local-only",
              "officeId": "local-only",
              "externalId": "synthetic-reference"
            }
            """;

    private RegisterCustomerUseCase registerCustomerUseCase;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerCustomerUseCase = mock(RegisterCustomerUseCase.class);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerCommandController(registerCustomerUseCase))
                .setControllerAdvice(new CifExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void closeValidator() {
        validator.close();
    }

    @Test
    void registrationPreservesRouteResponseAndMapsAllRequestFields() throws Exception {
        when(registerCustomerUseCase.registerCustomer(any()))
                .thenReturn(new RegisterCustomerResult(CUSTOMER_ID, "PENDING"));

        register(VALID_PAYLOAD)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"id":"00000000-0000-0000-0000-000000000123","status":"PENDING"}
                        """))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.customerNumber").doesNotExist());

        var command = ArgumentCaptor.forClass(RegisterCustomerCommand.class);
        verify(registerCustomerUseCase).registerCustomer(command.capture());
        assertEquals(new RegisterCustomerCommand(
                "Synthetic", "Customer", "synthetic@example.invalid", "local-only", "local-only",
                "synthetic-reference"), command.getValue());
    }

    @Test
    void registrationAllowsAbsentOptionalContactAndExternalReference() throws Exception {
        when(registerCustomerUseCase.registerCustomer(any()))
                .thenReturn(new RegisterCustomerResult(CUSTOMER_ID, "PENDING"));

        register("""
                {"firstName":"Synthetic","lastName":"Customer","officeId":"local-only"}
                """)
                .andExpect(status().isOk());

        var command = ArgumentCaptor.forClass(RegisterCustomerCommand.class);
        verify(registerCustomerUseCase).registerCustomer(command.capture());
        assertEquals(new RegisterCustomerCommand(
                "Synthetic", "Customer", null, null, "local-only", null), command.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "officeId"})
    void rejectsMissingRequiredFieldsBeforeCallingUseCase(String field) throws Exception {
        String payload = VALID_PAYLOAD.replaceAll("\\\"" + field + "\\\"\\s*:\\s*\\\"[^\\\"]*\\\",?", "");

        expectInvalidRequest(payload);
        verifyNoInteractions(registerCustomerUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "officeId"})
    void rejectsBlankRequiredFieldsBeforeCallingUseCase(String field) throws Exception {
        String payload = VALID_PAYLOAD.replaceAll("(\\\"" + field + "\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\" \"");

        expectInvalidRequest(payload);
        verifyNoInteractions(registerCustomerUseCase);
    }

    @Test
    void rejectsInvalidEmailWithSafeValidationResponse() throws Exception {
        expectInvalidRequest(VALID_PAYLOAD.replace("synthetic@example.invalid", "not-an-email"));
        verifyNoInteractions(registerCustomerUseCase);
    }

    @ParameterizedTest
    @ValueSource(strings = {"firstName", "lastName", "email", "phone", "officeId", "externalId"})
    void rejectsOversizedFieldsBeforeCallingUseCase(String field) throws Exception {
        int allowedLength = switch (field) {
            case "firstName", "lastName", "email" -> 255;
            default -> 50;
        };
        String payload = VALID_PAYLOAD.replaceAll(
                "(\\\"" + field + "\\\"\\s*:\\s*)\\\"[^\\\"]*\\\"", "$1\"" + "x".repeat(allowedLength + 1) + "\"");

        expectInvalidRequest(payload);
        verifyNoInteractions(registerCustomerUseCase);
    }

    @Test
    void duplicateEmailHasStableSafeConflictResponse() throws Exception {
        when(registerCustomerUseCase.registerCustomer(any()))
                .thenThrow(new DuplicateCustomerEmailException("synthetic.secret@example.invalid"));

        register(VALID_PAYLOAD)
                .andExpect(status().isConflict())
                .andExpect(content().json("""
                        {
                          "code":"CUSTOMER_EMAIL_ALREADY_EXISTS",
                          "message":"A customer with this email already exists."
                        }
                        """));
    }

    @Test
    void domainRejectionDoesNotExposeInternalDetails() throws Exception {
        when(registerCustomerUseCase.registerCustomer(any()))
                .thenThrow(new CustomerRegistrationRejectedException("synthetic internal rejection detail"));

        expectInvalidRequest(VALID_PAYLOAD);
    }

    @Test
    void notFoundHasStableSafeResponse() throws Exception {
        when(registerCustomerUseCase.registerCustomer(any()))
                .thenThrow(CustomerNotFoundException.byId(CUSTOMER_ID));

        register(VALID_PAYLOAD)
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {"code":"CUSTOMER_NOT_FOUND","message":"Customer not found."}
                        """));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "", "[]", "{\"firstName\":{\"private\":\"synthetic-only\"}}"})
    void malformedPayloadHasSafeBadRequestResponse(String payload) throws Exception {
        expectInvalidRequest(payload);
        verifyNoInteractions(registerCustomerUseCase);
    }

    @Test
    void commandControllerDoesNotExposeCustomerQueries() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{id}", CUSTOMER_ID))
                .andExpect(status().isNotFound());
        verifyNoInteractions(registerCustomerUseCase);
    }

    private ResultActions register(String payload) throws Exception {
        return mockMvc.perform(post(REGISTRATION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));
    }

    private void expectInvalidRequest(String payload) throws Exception {
        register(payload)
                .andExpect(status().isBadRequest())
                .andExpect(content().json("""
                        {
                          "code":"INVALID_CUSTOMER_REQUEST",
                          "message":"The customer registration request is invalid."
                        }
                        """));
    }
}
