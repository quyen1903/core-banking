package com.quinnbank.core.cif.api;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Opt-in check against the existing local schema; every synthetic write is rolled back. */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/core_banking",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=false"
})
@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
@EnabledIfEnvironmentVariable(named = "CIF_LOCAL_SMOKE_TEST", matches = "true")
class CustomerLocalSmokeTest {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;

    private UUID createdId;

    @Test
    void registersReadsAndUpdatesCustomerWithoutPersistedFullNameOrExternalId() throws Exception {
        String email = "synthetic-" + UUID.randomUUID() + "@example.invalid";
        String registration = """
                {"firstName":"Synthetic","lastName":"Customer",
                 "officeId":"local-only","email":"%s"}
                """.formatted(email);

        String body = mvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        createdId = UUID.fromString(JsonPath.read(body, "$.id"));

        mvc.perform(get("/api/v1/customers/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Synthetic Customer"))
                .andExpect(jsonPath("$.email").value(email));

        mvc.perform(patch("/api/v1/customers/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Updated Synthetic","phone":"local-only"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"));

        mvc.perform(get("/api/v1/customers/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Synthetic Customer"))
                .andExpect(jsonPath("$.phoneNumber").value("local-only"));

        assertEquals(1L, jdbc.queryForObject(
                "select version from customers where id = ?", Long.class, createdId));

        mvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON).content(registration))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CUSTOMER_EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void registrationStillRejectsMissingFirstName() throws Exception {
        mvc.perform(post("/api/v1/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lastName":"Customer","officeId":"local-only"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_CUSTOMER_REQUEST"));
    }

    @AfterTransaction
    void syntheticCustomerWasRolledBack() {
        if (createdId != null) {
            assertEquals(0, jdbc.queryForObject(
                    "select count(*) from customers where id = ?", Integer.class, createdId));
        }
    }
}
