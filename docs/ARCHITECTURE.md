# QuinnBank Core Architecture

## Architecture Style

QuinnBank Core uses a modular monolith with Hexagonal Architecture, DDD-lite,
CQRS-lite, and Fineract-inspired banking boundaries.

This means:

- modules are bounded contexts inside one deployable application;
- HTTP, persistence, generators, and integrations are adapters;
- application services orchestrate use cases through ports;
- domain models protect invariants without Spring MVC, JPA repositories, or API
  DTOs;
- commands and queries are separated when they have different authorization,
  transaction, or read-model needs;
- banking workflows follow explicit lifecycle, idempotency, audit, and
  reconciliation rules.

Fineract-inspired means the repo borrows conservative banking module boundaries
and lifecycle discipline. It does not claim compatibility with, certification
from, or compliance through Apache Fineract.

## Dependency Direction

```text
api -> application.port.in
api -> application.command/query/result
api -> api.dto/mapper

application.service -> application.port.out
application.service -> domain

infrastructure -> application.port.out
infrastructure -> domain
```

Forbidden directions:

- controller -> repository;
- application -> Spring Data repository;
- domain -> Spring, JPA, HTTP, SQL, external SDK;
- account -> CIF repository;
- one module -> another module's persistence model.

## Module Template

```text
com.quinnbank.core.<module>
├── api
│   ├── command
│   ├── query
│   ├── dto
│   └── mapper
├── application
│   ├── command
│   ├── query
│   ├── port
│   │   ├── in
│   │   └── out
│   ├── result
│   └── service
├── domain
│   ├── model
│   ├── policy
│   ├── event
│   └── exception
└── infrastructure
    ├── persistence
    ├── generator
    ├── integration
    └── messaging
```

Small modules may omit empty folders, but they must not collapse business logic
into controllers or application services that directly call Spring Data.

## Account Boundary

Account owns:

- account products;
- account lifecycle;
- account identifiers;
- account status;
- account-opening idempotency.

Account does not own:

- customer profile or KYC state;
- ledger postings;
- balance mutations after opening;
- transfers, payments, fees, or interest accrual;
- authentication identity.

Account depends on CIF through a customer lookup port implemented by an
infrastructure adapter around `CustomerDirectory`.

## Runtime

Spring virtual threads are enabled for request handling through
`spring.threads.virtual.enabled=true`. Virtual threads improve blocking request
scalability, but they do not replace JDBC pool limits, transaction discipline,
timeouts, backpressure, or idempotency.
