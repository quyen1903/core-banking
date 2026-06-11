# QuinnBank Engineering Principles

## DRY

Do not duplicate business rules, authorization decisions, error codes,
idempotency checks, masking rules, money rounding rules, or audit action names.

Do not create generic abstractions for incidental duplication. A duplicated
line of mapping code is cheaper than a misleading base service that hides a
financial rule.

## SOLID

Single Responsibility:

- controllers adapt HTTP;
- mappers translate DTOs;
- application services orchestrate use cases;
- ports describe required capabilities;
- domain models and policies protect business invariants;
- infrastructure adapters handle persistence and external systems.

Open/Closed:

- add policies or strategies for real banking variation such as eligibility,
  limits, fees, or interest;
- avoid frameworks for hypothetical variation.

Liskov:

- adapters must preserve port contracts, including failure, retry, and
  idempotency semantics.

Interface Segregation:

- ports must be narrow and use-case oriented;
- do not expose another module's full service or repository surface.

Dependency Inversion:

- application and domain depend on ports and domain concepts;
- infrastructure depends on application ports and implements them.

## Dependency Injection

Use constructor injection. Do not use field injection, service locators,
`ApplicationContext` lookups in business code, or static mutable dependencies.

Domain policies can be plain objects. When an application service needs one,
wire it through configuration so dependencies remain explicit.

## Exceptions And Try/Catch

Use specific exceptions for expected business failures. Map them centrally at
the API boundary.

Allowed `try/catch` cases:

- translating a low-level adapter exception into a port-level failure;
- adding safe context before rethrowing;
- compensating for a specifically designed partial failure path.

Forbidden `try/catch` cases:

- broad `catch (Exception)` that returns success;
- swallowing failed financial commands;
- hiding authorization or validation failure;
- converting infrastructure failures into ambiguous account state.

## Banking Domain Modeling

Use DDD-lite:

- aggregate methods express state transitions;
- value objects validate money, account numbers, and other identifiers;
- policies hold reusable business decisions;
- domain events are past-tense facts after successful transitions.

Use CQRS-lite:

- commands mutate state and own transaction/idempotency semantics;
- queries read state and should not mutate;
- split controllers/services when command and query concerns diverge.

Use Hexagonal Architecture:

- inbound ports are use cases;
- outbound ports are persistence, lookup, generator, messaging, and integration
  capabilities;
- adapters are replaceable without changing use-case logic.
