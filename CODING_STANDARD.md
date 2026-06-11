# QuinnBank Core Coding Standard

> Mandatory read order: `AGENTS.md` -> `SECURITY.md` -> `LIBRARY.md` ->
> this file -> `docs/ARCHITECTURE.md` -> `docs/ENGINEERING_PRINCIPLES.md` -> relevant `docs/architecture/*`.
>
> This standard is mandatory for implementation and review.

---

## 1. Engineering Bar

QuinnBank Core is not a CRUD demo. The bar is:

- deterministic financial behavior;
- explicit security and authorization;
- auditable state changes;
- controlled data access;
- safe operations;
- code that senior engineers can maintain under pressure.

Financial correctness outranks delivery speed.

---

## 2. Non-Negotiable Rules

The codebase MUST NOT:

- use `double` or `float` for money, balances, fees, rates, limits, settlement,
  reconciliation, or ledger values;
- return JPA entities from public APIs;
- put DTOs inside controllers;
- call repositories from controllers;
- call Spring Data repositories from application services;
- expose full account numbers or sensitive identifiers;
- use public setters on aggregates for invariant-bearing state;
- mutate financial state without transaction boundary and idempotency design;
- modify applied migrations;
- swallow failures with broad `catch (Exception)`;
- disable security globally for convenience;
- use field injection or service locator patterns;
- introduce generic base services/controllers that hide domain behavior.

The codebase MUST:

- use explicit module boundaries;
- map HTTP DTOs into commands/queries;
- use use-case ports;
- use outbound ports for persistence/integration;
- centralize safe error mapping;
- mask sensitive values;
- make financial state transitions explicit;
- add tests proportional to risk.


---

## 3. SOLID, DRY, DI, And Pattern Rules

General design principles are mandatory for QuinnBank, but they must serve
financial correctness, security, auditability, and module boundaries. Detailed
rules live in `docs/ENGINEERING_PRINCIPLES.md`.

Minimum rules:

- Single Responsibility: controllers adapt HTTP, application services orchestrate
  use cases, domain models protect invariants, infrastructure adapts external
  systems. Do not mix these responsibilities.
- Open/Closed: use named policies or strategies for real product variation such
  as fees, interest, limits, and eligibility. Do not build abstract frameworks
  for hypothetical variation.
- Liskov: adapter and strategy implementations must preserve their port or
  policy contract, including failure and idempotency semantics.
- Interface Segregation: ports must be narrow and use-case specific. Do not
  expose another module's internal service surface.
- Dependency Inversion: application/domain code depends on ports and domain
  concepts, not Spring Data repositories, HTTP clients, SDKs, or JPA details.
- DRY: duplicate no business rule, permission, error code, idempotency rule,
  money rounding rule, account masking rule, or audit action name. Do not create
  generic abstractions for coincidental duplication.
- Dependency Injection: constructor injection only; no field injection, service
  locator, static mutable dependency, or `ApplicationContext` usage in business
  code.
- Design Patterns: use patterns only when they make a banking rule clearer,
  safer, or more testable. Prefer Ports and Adapters, Policy, Strategy, State
  Machine, Domain Event, Outbox, Mapper, and Factory Method. Avoid generic
  `BaseService`, `BaseController`, broad `Manager`, and pattern cosplay.

---

## 4. Package Layout

Default module shape:

```text
com.quinnbank.core.<module>
├── api
│   ├── command
│   ├── query
│   ├── dto
│   │   ├── request
│   │   └── response
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
│   ├── value
│   ├── policy
│   ├── event
│   └── exception
└── infrastructure
    ├── persistence
    ├── integration
    ├── messaging
    └── configuration
```

Small modules may omit empty folders. They must not collapse into
controller-service-repository MVC.

---

## 5. Layer Responsibilities

| Layer | Owns | Must not own |
| --- | --- | --- |
| `api` | HTTP routes, DTOs, shallow validation, HTTP status, HTTP mapper | business decisions, transactions, repository calls |
| `application` | use cases, commands, queries, authorization invocation, transaction boundary, orchestration | HTTP objects, SQL, persistence schema |
| `domain` | aggregates, value objects, invariants, policies, state machines, domain exceptions | Spring MVC, API DTOs, JPA repositories, SQL, external SDKs |
| `infrastructure` | JPA/JDBC, persistence adapters, external clients, messaging, clocks, id generators | hidden business policy |

Dependency direction:

```text
api -> application
application -> domain
application -> application.port.out
infrastructure -> application.port.out
infrastructure -> domain
```

---

## 6. Naming Standards

Use business names.

| Type | Pattern | Example |
| --- | --- | --- |
| Controller | Context + command/query + `Controller` | `AccountCommandController` |
| Request DTO | Intent + `Request` | `OpenAccountRequest` |
| Response DTO | Resource + `Response` | `AccountResponse` |
| Mapper | Context + `HttpMapper` | `AccountHttpMapper` |
| Use case | Verb phrase + `UseCase` | `OpenAccountUseCase` |
| Command | Intent + `Command` | `OpenAccountCommand` |
| Query | Read intent + `Query` | `GetAccountSummaryQuery` |
| Result/snapshot | Resource + `Snapshot` | `AccountSnapshot` |
| Out port | Capability + `Port` | `BankAccountRepositoryPort` |
| Adapter | Technology + capability + `Adapter` | `BankAccountPersistenceAdapter` |
| Policy | Decision + `Policy` | `AccountOpeningPolicy` |
| Event | Past tense business fact | `AccountOpened` |
| Exception | Business failure + `Exception` | `AccountOpeningRejectedException` |

Avoid `Manager`, `Helper`, `Util`, `Processor`, `Common`, `Data`, and `Info`
unless there is no business name.

---

## 7. API Standard

Controllers are adapters.

Controllers MUST:

- use versioned routes, such as `/api/v1/accounts`;
- be split into command/query controllers when meaningful;
- validate request shape, size, type, and basic format;
- map request DTOs to application commands/queries;
- call a use-case port;
- map result snapshots to response DTOs;
- return safe HTTP responses.

Controllers MUST NOT:

- contain business policy;
- call repositories;
- create aggregates directly for business use cases;
- calculate money;
- mask data inline when a mapper exists;
- expose internal exception messages;
- nest DTOs.

Preferred account API files:

```text
api/command/AccountCommandController.java
api/query/AccountQueryController.java
api/dto/request/OpenAccountRequest.java
api/dto/response/AccountResponse.java
api/mapper/AccountHttpMapper.java
```

---

## 8. Application Standard

Use cases are the public application boundary.

Rules:

- Inbound use-case interfaces live under `application.port.in`.
- Outbound ports live under `application.port.out`.
- Application services live under `application.service` and implement inbound
  ports.
- `@Transactional` belongs on application service methods or class.
- Application services orchestrate; they do not own persistence details.
- Application services return result objects or snapshots, not aggregates when
  the API should not mutate them further.

Financial commands MUST handle:

- idempotency;
- concurrency;
- transaction boundary;
- safe failure behavior;
- audit or event recording.

---

## 9. Domain Standard

Aggregates protect valid state.

Rules:

- Constructors/factories create valid objects.
- State transitions use named methods.
- Public setters are forbidden for invariant-bearing fields.
- Value objects validate themselves.
- Domain policies hold reusable business decisions.
- Domain exceptions represent expected business failures.
- Domain events are past-tense business facts.
- Domain code must not depend on Spring MVC, HTTP DTOs, SQL strings, or external
  clients.

Bad:

```java
account.setStatus(AccountStatus.CLOSED);
account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
```

Good:

```java
account.close(reason, actor, clock.instant());
account.reserveWithdrawal(amount, commandId, clock.instant());
```

---

## 10. Persistence And Migration Standard

Migrations are production artifacts.

Rules:

- Use Flyway for schema changes.
- Applied migrations are forward-only.
- Table and column names use lower_snake_case.
- Monetary columns use explicit `NUMERIC(p, s)`.
- Mutable business tables include `created_at`, `updated_at`, and `version`.
- Status columns correspond to explicit application/domain state machines.
- Foreign keys and indexes must match owned relationships and query paths.
- Do not use `ddl-auto` to mutate shared databases.

Repository rules:

- Spring Data repositories live in `infrastructure.persistence`.
- Persistence adapters implement outbound repository ports.
- Mappers convert between JPA entities and domain models if entities are split.
- Cross-module repository access is forbidden.

---

## 11. Money, Currency, Rates, And Rounding

Mandatory:

- Use `BigDecimal` or a reviewed money value object.
- Currency is explicit, preferably ISO 4217.
- Rounding mode is explicit at calculation boundaries.
- Do not compare `BigDecimal` with `equals()` when scale is irrelevant.
- Do not mix currencies in arithmetic.
- Persist calculated values needed for audit and historical display.
- Rates must define scale, rounding, effective date, and calculation basis.

---

## 12. Ledger And Balance Rules

Ledger code has the highest correctness bar.

Rules:

- Ledger entries should be immutable.
- Corrections use reversal or adjustment entries.
- Debits and credits balance by currency.
- Posting workflow has explicit states.
- Balance projections reconcile to ledger entries.
- Manual adjustments require reason and audit evidence.
- Posting idempotency keys are unique.
- Reversal must not double-credit or double-debit under retry.

Minimum posting fields:

```text
journal id
source command id
idempotency key
actor/system
business reason
posting date
value date
currency
debit lines
credit lines
correlation id
```

---

## 13. Idempotency Standard

Retriable commands that create or mutate state MUST be idempotent.

Examples:

- open account;
- create transfer;
- post ledger entry;
- apply fee;
- process webhook;
- reverse transaction;
- import batch file.

Idempotency record should include:

- key or external event id;
- actor/source system;
- command fingerprint;
- first result status;
- response reference or result id;
- replay behavior;
- retention rule.

Same key with different fingerprint is a conflict.

---

## 14. Authorization Standard

Authorization belongs in application flow, not only route annotations.

A decision must answer:

1. who is the actor;
2. what action is requested;
3. which resource is targeted;
4. whether the resource is in scope;
5. whether context requires denial, step-up, or maker-checker.

Never trust client-supplied role, customer id, account id, branch, permission,
balance, fee, risk status, or payment status.

---

## 15. Exception And Error Standard

Expected business failures should use specific exceptions.

Examples:

```text
CustomerNotActiveException
AccountProductInactiveException
AccountOpeningRejectedException
DuplicateCommandConflictException
InsufficientFundsException
AccountBlockedException
```

External error response shape:

```json
{
  "code": "ACCOUNT_BLOCKED",
  "message": "The account cannot perform this operation.",
  "correlationId": "01HX..."
}
```

Do not return stack traces, SQL names, hostnames, secrets, policy internals, or
protected-resource existence hints.

---

## 16. Logging, Audit, And Observability

Logs are diagnostics. Audit is evidence.

Logs:

- include correlation id;
- do not include secrets;
- do not include raw request/response bodies for identity or financial endpoints;
- mask account numbers and sensitive identifiers.

Audit events should exist for:

- customer creation/status changes;
- account opening/status changes;
- product/rate/fee changes;
- transfer acceptance/rejection/posting/reversal;
- manual ledger adjustment;
- authorization denial for sensitive resources;
- role/permission changes;
- exports and bulk reads.

---

## 17. Testing Standard

Tests scale with risk.

| Area | Minimum tests |
| --- | --- |
| Domain state transition | valid and invalid transition unit tests |
| Use case | transaction, idempotency, authorization, duplicate behavior |
| API | validation, status code, response contract, safe error |
| Persistence | mapping, constraints, migration validation |
| Security | positive and negative access tests |
| Financial workflow | amount boundary, currency mismatch, concurrency, reversal |

Test data must be synthetic. Do not use real customer names, emails, phone
numbers, account numbers, cards, KYC documents, or transaction histories.

---

## 18. Review Blockers

Block code that:

- mutates financial state without transaction and idempotency;
- uses unsafe money types;
- exposes sensitive data;
- bypasses authorization/resource scoping;
- returns JPA entities from API;
- bypasses domain transitions;
- modifies applied migrations;
- lacks tests for high-risk behavior;
- hides failure with generic exception handling;
- adds dependency without `LIBRARY.md` justification.

---

## 19. Definition Of Done

A change is done when:

- correct module owns the behavior;
- boundaries follow `docs/ARCHITECTURE.md`;
- business rules are explicit;
- security and financial impact are considered;
- relevant tests pass or gaps are documented;
- migration impact is reviewed;
- logs and errors are safe;
- audit requirements are satisfied;
- documentation is updated when contracts change.
