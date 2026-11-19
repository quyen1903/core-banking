# core-banking

## Auth: Implementation Guides and Next Steps

- [Start here: auth implementation checklist by table](docs/architecture/auth-implementation-guide.md).
- [Proposed database design, relationships, and PostgreSQL DDL](docs/architecture/auth-session-schema-proposal.md).
- [Identity module boundaries and target APIs](docs/architecture/identity-module-design.md).

Current direction: Spring Security with server-side sessions stored in
PostgreSQL through Spring Session JDBC. These documents describe the design
and remaining work; an entity or draft DDL does not mean a feature is implemented.

```text
src/
├── main/
│   ├── java/com/quinnbank/core/        # Project structure follows the CQRS pattern
│   │   ├── QuinnBankApplication.java
│   │   │
│   │   ├── customer/                         # Business module
│   │   │   ├── domain/            # Business model, rules, and state
│   │   │   │   ├── model/
│   │   │   │   │   └── Customer.java
│   │   │   │   ├── valueobject/
│   │   │   │   │   └── CustomerId.java
│   │   │   │   ├── policy/
│   │   │   │   │   └── CustomerActivationPolicy.java
│   │   │   │   └── exception/
│   │   │   │       └── CustomerRegistrationRejectedException.java
│   │   │   │
│   │   │   ├── application/        # Orchestrate use cases and define ports
│   │   │   │   ├── port/
│   │   │   │   │   ├── in/     # Inbound calls from HTTP, CLI, message consumers, etc.
│   │   │   │   │   │   ├── RegisterCustomerUseCase.java
│   │   │   │   │   │   └── GetCustomerByIdUseCase.java
│   │   │   │   │   └── out/    # Outbound operations: database access, API calls, messaging, etc.
│   │   │   │   │       ├── CustomerWritePort.java
│   │   │   │   │       ├── CustomerReadPort.java
│   │   │   │   │       └── CustomerNumberGeneratorPort.java
│   │   │   │   ├── command/
│   │   │   │   │   └── RegisterCustomerCommand.java
│   │   │   │   ├── query/
│   │   │   │   │   └── GetCustomerByIdQuery.java
│   │   │   │   ├── result/
│   │   │   │   │   ├── RegisterCustomerResult.java
│   │   │   │   │   └── CustomerDetails.java
│   │   │   │   └── service/
│   │   │   │       ├── RegisterCustomerService.java
│   │   │   │       └── GetCustomerByIdService.java
│   │   │   │
│   │   │   ├── adapter/
│   │   │   │   ├── in/
│   │   │   │   │   └── web/
│   │   │   │   │       ├── CustomerController.java
│   │   │   │   │       ├── CustomerExceptionHandler.java
│   │   │   │   │       ├── dto/
│   │   │   │   │       │   ├── RegisterCustomerRequest.java
│   │   │   │   │       │   └── RegisterCustomerResponse.java
│   │   │   │   │       └── mapper/
│   │   │   │   │           └── CustomerWebMapper.java
│   │   │   │   └── out/
│   │   │   │       ├── persistence/
│   │   │   │       │   ├── CustomerWritePersistenceAdapter.java
│   │   │   │       │   ├── CustomerReadPersistenceAdapter.java
│   │   │   │       │   ├── CustomerJpaEntity.java
│   │   │   │       │   ├── CustomerJpaRepository.java
│   │   │   │       │   └── CustomerPersistenceMapper.java
│   │   │   │       └── generator/
│   │   │   │           └── DefaultCustomerNumberGenerator.java
│   │   │   │
│   │   │   └── configuration/      # Initialize and wire services with Spring
│   │   │       ├── CustomerConfiguration.java
│   │   │       └── TransactionalRegisterCustomerUseCase.java
│   │   │
│   │   ├── account/         
│   │   └── ledger/
│   │
│   └── resources/
│       ├── application.yml
│       └── db/migration/
│
└── test/java/com/quinnbank/core/customer/
    ├── domain/
    ├── application/
    ├── adapter/
    │   ├── in/web/
    │   └── out/persistence/
    └── architecture/


Dependency direction
Web Controller ──────────────> Input Port
Application Service ─────────> Input Port       (implements)
Application Service ─────────> Domain
Application Service ─────────> Output Port
Persistence Adapter ─────────> Output Port      (implements)
Persistence Adapter ─────────> JPA Repository
Configuration ───────────────> Service + Adapter wiring

```

### Customer Persistence Flow

1. The application service creates a `Customer` domain object through its factory method, which enforces the domain's business rules.
2. The application service calls `CustomerWritePort.save(customer)`.
3. `CustomerWritePersistenceAdapter`, which implements `CustomerWritePort`, handles the call.
4. The adapter uses `CustomerPersistenceMapper` to convert the domain object into a new `CustomerJpaEntity`.
5. The adapter calls `CustomerJpaRepository.saveAndFlush(entity)`.
6. Spring Data JPA delegates persistence to Hibernate, which executes the SQL needed to synchronize the entity with the database.

> **Note:** `flush` does not commit the transaction. The changes become committed only when the enclosing transaction commits successfully.
