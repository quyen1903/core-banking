# CIF Module Design

## Scope and risk

CIF owns the customer master record. This refactor is R3 because it handles
customer-confidential data. It separates registration commands from customer
queries and isolates persistence and HTTP without adding a broker, event sourcing,
another database, or a new dependency.

The refactor preserves the existing registration route, HTTP status, response
fields and initial lifecycle values. It does not introduce account, payment,
KYC decision, or customer lifecycle transition workflows.

## Dependency direction

```text
api -> application ports, commands and results
application services -> domain and outbound ports
infrastructure -> application ports and domain
domain -> JDK only
```

Both the application and domain compile without Spring, Jakarta, JPA, Lombok or
adapter classes. JUnit architecture tests verify this using the JDK compiler with
an empty dependency classpath.

```text
cif
  api
    command/CustomerCommandController
    query/CustomerQueryController
    dto/request/RegisterCustomerRequest
    dto/response/RegisterCustomerResponse
    dto/response/GetCustomerByIdResponse
    mapper/CustomerHttpMapper
    CifExceptionHandler
  application
    command/RegisterCustomerCommand
    query/GetCustomerByIdQuery
    result/RegisterCustomerResult
    result/GetCustomerByIdResult
    port/in/RegisterCustomerUseCase
    port/in/GetCustomerByIdUseCase
    port/out/CustomerWritePort
    port/out/CustomerReadPort
    port/out/CustomerNumberGeneratorPort
    service/CustomerCommandService
    service/CustomerQueryService
  domain
    model/Customer
    enums/CustomerStatus, KycStatus, RiskRating
    exception/CustomerRegistrationRejectedException
  infrastructure
    configuration/CifUseCaseConfiguration
    configuration/TransactionalRegisterCustomerUseCase
    configuration/TransactionalGetCustomerByIdUseCase
    persistence/CustomerJpaEntity
    persistence/CustomerJpaRepository
    persistence/CustomerPersistenceMapper
    persistence/CustomerWritePersistenceAdapter
    persistence/CustomerReadPersistenceAdapter
    generator/SystemClockCustomerNumberGenerator
```

The unused duplicate enums, aggregate-returning query repository, persistence
method that recreated a customer, and unsupported command builder were removed.
There are no active cross-module CIF consumers in the current Java source tree;
future consumers must use a narrow application contract.

## Registration command

```text
POST /api/v1/customers/register
  -> RegisterCustomerRequest + HTTP validation
  -> CustomerHttpMapper
  -> RegisterCustomerUseCase
  -> transaction decorator
  -> CustomerCommandService
  -> CustomerWritePort.existsByEmail(normalizedEmail), when non-null
  -> CustomerNumberGeneratorPort
  -> Customer.register(...)
  -> CustomerWritePort.save(...)
  -> JPA entity + saveAndFlush
  -> RegisterCustomerResult
  -> RegisterCustomerResponse
```

The existing response remains HTTP 200 with `id` and `status`. Success status
is `PENDING`; KYC starts at `NOT_STARTED` and risk at `LOW`.

HTTP request validation belongs to `RegisterCustomerRequest`. The domain
enforces required names/office/customer number, field lengths, normalization and
the required registration timestamp for every caller. It has no public setters.
Transport-level email syntax validation remains on the HTTP DTO.

Email normalization uses trimming and `Locale.ROOT` lowercasing before both the
lookup and persistence. Blank/absent email becomes null and skips the uniqueness
precheck. The database unique constraint handles concurrent inserts. Only SQLSTATE
23505 for `customers_email_key` becomes `DuplicateCustomerEmailException`;
customer-number collisions and unrelated integrity failures remain failures.

The response adapter maps invalid input to a fixed safe 400 message and duplicate
email to the existing `CUSTOMER_EMAIL_ALREADY_EXISTS` 409 code. Exception text,
submitted email addresses and SQL details are not returned.

`officeId` is required metadata (maximum 50 characters), and `externalId` is
optional source metadata (maximum 50 characters). Both are trimmed and persisted.
This fixes their previous silent omission: a caller-provided external ID is now
retained instead of being replaced by a random UUID. Neither field grants
authorization, proves office existence, nor establishes external-ID uniqueness.
Those policies require explicit future design.

The customer UUID is generated internally. The existing customer-number format
(`CIF` + UTC timestamp + eight UUID characters) is retained, with an injected
clock and database uniqueness enforcement. A collision fails registration; this
refactor does not add a retry policy or replace it with a sequence.

## Customer query

```text
GET /api/v1/customers/{id} (local profile only)
  -> CustomerQueryController + UUID validation
  -> CustomerHttpMapper
  -> GetCustomerByIdUseCase(GetCustomerByIdQuery)
  -> read-only transaction decorator
  -> CustomerQueryService
  -> CustomerReadPort
  -> JPQL immutable DTO projection
  -> GetCustomerByIdResult
  -> CustomerHttpMapper
  -> GetCustomerByIdResponse
```

The query handler does not load or reconstruct a domain aggregate, call the write
port, or expose a JPA entity. Missing customers raise a safe
`CustomerNotFoundException`. Results contain customer ID, first/last names,
email, phone number and full name.

The HTTP adapter is available only when `local` is active and none of `dev`,
`test`, `qa`, `uat`, `staging`, `pre-prod`, or `prod` is active. The `local` profile section of
`application.yml` binds the server to `127.0.0.1`. This is an unauthenticated development endpoint
for synthetic customer data on an isolated local machine. Do not override the
loopback binding or expose this profile through a proxy, tunnel, or shared
runtime. Profile gating is not authentication or resource authorization.

The successful HTTP 200 response contains only `id`, `firstName`, `lastName`,
`email`, `phoneNumber`, and `fullName`. It includes `Cache-Control: no-store`.
Missing customers return HTTP 404 with `CUSTOMER_NOT_FOUND`; malformed UUIDs
return HTTP 400 with `INVALID_CUSTOMER_ID` before the use case is invoked.
Error messages do not include submitted identifiers or internal exception details.

Authentication and resource-scoped authorization remain unimplemented. Shared
customer reads must authenticate the actor and enforce scope before invoking the
application query. The local endpoint does not establish an authorization model,
add audit storage, or change the database schema or dependencies.

With a disposable local database containing synthetic customers, start the
application and query the ID returned by registration:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
curl -i http://127.0.0.1:8080/api/v1/customers/00000000-0000-0000-0000-000000000123
```

The example UUID is synthetic; it returns 404 unless that record exists locally.
See [environment activation](../operations/environments.md#4-activation) for the
local-profile constraints.

CQRS here means separate command/query contracts, handlers and outbound ports
sharing one transactional PostgreSQL database. It does not require separate
databases or eventual consistency.

## Transactions and failure behavior

The composition root supplies plain services with ports and the shared UTC
`Clock`. Explicit Spring decorators wrap registration in a write transaction
and queries in a read-only transaction. The transaction boundary encloses the
whole use case, while framework annotations remain outside the core.

Registration flushes within the transaction so known database uniqueness failures
can be translated safely before returning. Transaction failure propagates; no
success is returned after failed persistence. An enclosing transaction can roll
back the registration. Integration tests cover this and concurrent duplicate
email inserts.

Registration currently creates a new aggregate only. There is no generic
rehydration factory, mutable aggregate setter or update command. The JPA entity
uses a nullable `Long @Version` for new-entity detection; Hibernate initializes
version zero on insert. Future update workflows must preserve version checks and
introduce explicit domain transitions.

## Migration and rollout

[V7__separate_cif_customer_persistence.sql](../../src/main/resources/db/migration/V7__separate_cif_customer_persistence.sql)
is additive. V1–V6 remain unchanged.

The old domain entity expected columns absent from the migrations. V7 adds
nullable `first_name`, `last_name`, `office_id` and `external_id`, while
retaining the required `full_name` column and widening it to 511 characters
(two 255-character name components plus a space).

New registrations store both structured names and the composed full name.
Existing rows retain their exact full name, UUID, customer number, lifecycle
fields, timestamps, version and account references. Their new columns remain
null. Query consumers must display `fullName` when structured names are absent.
No automatic name splitting or invented office/external identifier is performed.

Existing primary/foreign keys and uniqueness constraints remain intact. No new
index is required for queries by UUID or the existing email lookup. No financial
column, balance or ledger entry is changed.

Apply V7 before deploying the new adapter. PostgreSQL ALTER TABLE requires a
table lock; schedule the migration and set deployment lock/time limits according
to the target environment. Production table size, workload and lock duration
were not evaluated. No data backfill is required by this migration.

Leave V7 in place if rolling back application code; any corrective schema change
must use a later migration. The previous Java mapping already disagreed with
V1–V6, so this refactor does not claim that reverting to that binary is a verified
operational rollback. Test the chosen rollback artifact before deployment.

## Remaining controls

The current build has Spring Security dependencies commented out. The customer
lookup adapter does not change that configuration; its unauthenticated HTTP
access is limited to the explicit local profile described above. Authentication,
resource/branch authorization, rate limiting and negative authorization tests
remain required before exposing customer workflows in a shared environment.
Local profile and loopback defaults reduce accidental exposure; they do not
protect against other processes on the same machine or unsafe deployment
configuration. No durable read-audit event is added by this lookup.

Registration also has no request idempotency record or durable audit event/store.
Unique email detects conflicting email values, not repeated requests or the same
person. Registrations without email can create distinct records on retry.
Customer creation audit, replay semantics and identity/office policy remain
separate work; no logging or domain event is presented as an audit substitute.

Previously stored mixed-case emails are not normalized or merged automatically.
The exact email constraint is retained. Any historical normalization requires a
reviewed duplicate-remediation plan before adding normalized database uniqueness.

## Verification

The test suite covers:

- domain invariants, optional fields, length boundaries and locale-independent
  normalization;
- command normalization, duplicate checks, clock injection and failure propagation;
- query snapshots, legacy names and missing-customer behavior;
- registration HTTP compatibility and validation;
- local customer lookup response fields, legacy names, safe 400/404 errors and
  cache prevention;
- customer lookup controller exclusion by default and for shared-environment
  profiles, including combinations with `local`;
- framework-independent core compilation and read-port separation;
- PostgreSQL mapping, metadata, version initialization, null emails, transaction
  rollback, concurrent duplicate email and customer-number conflicts;
- V1–V7 initialization and V6-to-V7 upgrade with synthetic legacy customer and
  bank-account references.

Fast tests, with no database connection:

```powershell
.\gradlew.bat test --tests 'com.quinnbank.core.cif.api.*' --tests 'com.quinnbank.core.cif.domain.*' --tests 'com.quinnbank.core.cif.application.*' --tests 'com.quinnbank.core.cif.architecture.*'
```

PostgreSQL tests require a disposable local database named `cif_refactor_test`
on `127.0.0.1`, a local test role `cif_test`, and the port in
`CIF_TEST_DB_PORT`. The dedicated test configuration replaces application
configuration and does not import the local `.env`. It uses only local
throwaway credentials and synthetic fixtures.

```powershell
$env:CIF_TEST_DB_PORT = '55439' # Port of the disposable test instance.
$env:SPRING_CONFIG_LOCATION = 'classpath:cif-integration.yml'
.\gradlew.bat test --console=plain
```

Without `CIF_TEST_DB_PORT`, PostgreSQL-specific tests are explicitly skipped.
The existing general context test still requires a configured database, so use
the filtered command above for database-free runs. Stop the disposable database
after verification; never point tests at a shared or production database.
