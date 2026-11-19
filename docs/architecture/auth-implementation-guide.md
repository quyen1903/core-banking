# Auth Module: Implementation Checklist by Table

This is an implementation checklist for **server-side sessions** using Spring
Security, Spring Session JDBC, and PostgreSQL. It describes work to be done;
it does not confirm that migrations, entities, APIs, or security controls are
complete. Mark an item complete only when the corresponding code and
verification results exist.

- [Detailed DDL and database design](auth-session-schema-proposal.md).
- [Identity module boundaries and target APIs](identity-module-design.md).
- [Database design for the authentication-first staff workflow](staff-authentication-db-design.md).
- [Back to the README](../../README.md).

The current scope starts with staff authentication, as described in the separate
document above. The checklist below still describes the overall auth target;
RBAC tables belong to the later authorization step. Until authorization exists,
do not expose business APIs merely because a user has logged in.

## Quick Reference

| Table | What question does it answer? | Work to implement |
| --- | --- | --- |
| `identity_user` | Who is logging in? Which customer/staff owner do they belong to? Are they active? | Entity, domain, migration, ports/adapters, and use cases |
| `identity_credential` | How is the password verified? Is a password change required? | Entity, encoder adapter, and password setup/change/reset use cases |
| `identity_role` | Which roles exist? | Entity, role catalog, migrations/seeds, and usage rules |
| `identity_permission` | Which actions can be authorized? | Entity, permission catalog, and application authorization checks |
| `identity_user_role` | Which roles are assigned to a user? | Association entity, constraints, assignment/revocation use cases, and audit |
| `identity_role_permission` | Which actions can a role perform? | Association entity, permission matrix, and permission-change handling |
| `spring_session` | Which login sessions are valid? | Migration and framework configuration; no custom JPA entity needed |
| `spring_session_attributes` | What minimal authentication information does a session hold? | Spring Session storage/retrieval; no public CRUD API |
| `audit_event` | Who did what, when, and with what outcome? | Shared audit capability, ports/adapters, durable storage, and database permissions |

A table does not imply a controller or a CRUD API. APIs represent business
actions, such as `DisableIdentityUseCase`, rather than allowing clients to
arbitrarily update status, role, or credential columns.

## Common Rules Before Writing Entities

- [ ] Reconcile migration history with the existing schema, especially `appuser`.
  This checkout currently has no SQL migrations in `src/main/resources/db/migration`,
  while older tests reference migration history. Verify that history before
  choosing a new version; do not recreate `V1` or edit applied migrations.
- [ ] Use the proposed DDL as a starting point for review. Do not run it directly
  against a populated database without a transition plan.
- [ ] Follow the existing identity design: internal `Long id`, API-facing
  `UUID publicId`. Internal auth FKs use `id`; owners use UUIDs supplied by
  their owning modules.
- [ ] Mutable business tables have `created_at`, `updated_at`, and `version`;
  map timestamps to `Instant` / `timestamptz` and versions with `@Version`.
  Sessions follow the framework schema contract; audit records are immutable.
- [ ] Migrations declare the required `NOT NULL`, PK, FK, unique, check, and
  index definitions. `@NotNull` and entity annotations do not replace migration
  constraints.
- [ ] Use domain methods for state transitions. Application code calls outbound
  ports; only persistence adapters use Spring Data repositories. HTTP returns
  DTOs/snapshots.
- [ ] Every sensitive use case checks the actor, permissions, and resource scope.
  Place the transaction boundary at the application layer and record audit
  evidence for changes that require it.

## identity_user

**Purpose:** the login identity and its link to the business owner.

Proposed fields: `id`, `public_id`, `username`, `username_normalized`,
`owner_type`, `owner_id`, `status`, `security_version`, `created_at`,
`updated_at`, and `version`.

- [x] Update the JPA mapping of
  [IdentityUserJpaEntity](../../src/main/java/com/quinnbank/core/auth/adapter/out/persistence/identity_user/IdentityUserJpaEntity.java)
  from `appuser` to `identity_user`, with an internal `Long` ID, public UUID,
  owner, status, security version, and audit timestamps. This is the target
  mapping; the database has not been migrated.
- [ ] Create a migration with unique `public_id`, unique `username_normalized`,
  an index on `(owner_type, owner_id)`, and status/owner-type checks.
- [ ] Use the same username normalization rules for registration and login. The
  draft DDL proposes an ASCII character set and case-insensitive matching;
  finalize the rules before implementation.
- [ ] `owner_type` is `CUSTOMER` or `STAFF`; verify the owner through a CIF/staff
  application contract. Do not call another module's repository directly.
- [ ] Define transitions between `PENDING_ACTIVATION`, `ACTIVE`, and `DISABLED`,
  including activation conditions. Do not grant full access before the required
  credentials/MFA are complete.
- [ ] Create use cases to provision, read snapshots, activate, and disable
  identities. Input must not let clients choose administrative permissions or
  change the security version themselves.
- [ ] Distinguish `version`, which protects against concurrent overwrites, from
  `security_version`, which detects stale sessions. Disabling an identity must
  update its security version and audit evidence in the same transaction.
- [x] Move the password and password-change requirement to the credential;
  replace the old status flags with identity status and the login-state table.
  The user confirmed that no local data needs preserving; this change has not
  migrated a database where V6 was applied.
- [ ] Test duplicate normalized usernames, invalid owners, invalid state
  transitions, out-of-scope access, update conflicts, and sessions belonging
  to disabled users.

**Decision required:** how many identities an owner may have. One owner per
identity does not imply one identity per owner. `owner_type + owner_id` is a
logical link; the current SQL does not prove that the owner exists in CIF/staff.

## identity_credential

**Purpose:** store one current password credential per identity.

Fields: `user_id` as both PK and FK, `password_hash`, `password_changed_at`,
`must_change_password`, timestamps, and `version`.

- [x] Complete the `IdentityCredentialJpaEntity` mapping: shared PK/FK `user_id`,
  hash, `Instant passwordChangedAt`, password-change requirement, timestamps,
  and version. Do not use the entity as a request/response DTO; the database
  migration is still outstanding.
- [ ] Add an FK to `identity_user.id`, ensuring at most one credential per user
  in the current password model. A pending user may not yet have a credential.
- [ ] Use an approved password encoder through an adapter and store the full
  encoded hash. Check that the column length fits the actual encoding; do not
  implement custom hashing or salt generation.
- [ ] Write initial password setup, password change, and password reset use
  cases. Distinguish a user's own password change from an authorized reset
  performed by another actor.
- [ ] On change/reset, update the hash, timestamp, password-change requirement,
  `identity_user.security_version`, and audit in a controlled transaction.
- [ ] When `must_change_password=true`, allow only the steps needed to change
  the password/complete authentication; do not open all business APIs.
- [ ] Passwords/hashes must not appear in public DTOs, logs, session principals,
  or audit payloads. The domain must not depend on Spring Security or JPA.
- [ ] Test missing credentials, incorrect passwords, unauthorized changes/resets,
  rejection of stale sessions, and rollback on credential/audit write failure.

Single-use reset tokens, token lifetimes, and MFA recovery require a separate
design. Do not store raw tokens or MFA secrets in this password-credential table.

## identity_role

**Purpose:** the role catalog, initially designed around `CUSTOMER`, `TELLER`,
and `ADMIN`. Creating a catalog role does not assign it to any user.

- [ ] Create the entity and migration: ID, unique `code`, description, `enabled`,
  timestamps, and `version`. Map codes by explicit names, not enum ordinals.
- [ ] Seed the catalog through a reviewed migration. Do not seed users or
  default passwords, or automatically assign `ADMIN`.
- [ ] Define who may manage the catalog and a controlled first-operator
  bootstrap process with audit and required authentication. Do not expose a
  public endpoint that grants admin access.
- [ ] Permission resolution excludes disabled roles. Role changes must affect
  users holding that role, including users with existing sessions.
- [ ] Test duplicate codes, missing/disabled roles, and unauthorized changes.

## identity_permission

**Purpose:** the catalog of actions actually checked by the application.

- [ ] Create the entity and migration: ID, unique `code`, description, `enabled`,
  timestamps, and `version`.
- [ ] Define codes around use-case actions; proposed examples include
  `IDENTITY_USER_READ`, `IDENTITY_USER_DISABLE`, and `IDENTITY_ROLE_ASSIGN`.
  These are examples for review, not an approved permission matrix.
- [ ] Link each code to an explicit application authorization check. Permission
  to read a user still requires checking that the user is within the allowed
  resource scope.
- [ ] Permission resolution excludes disabled permissions; protect and audit
  catalog changes.
- [ ] Test missing permissions, incorrect scope, and disabled permissions.
  Include both allowed and denied paths.

## identity_user_role

**Purpose:** assign roles to identities, with each `(user_id, role_id)` pair
appearing at most once.

- [ ] Create an association entity with a composite key (`@EmbeddedId` or
  `@IdClass`), timestamps, and `@Version`; the composite PK also prevents
  duplicate pairs.
- [ ] Create two FKs with restricted deletion and a `role_id` index for finding
  users by role.
- [ ] Write role assignment/revocation use cases that check actor, owner type,
  and scope. Client input must not grant staff/admin roles to customer identities.
- [ ] Control changes through the identity aggregate. Use concurrency control
  on the identity when protecting rules across multiple assignments. Versions
  on individual association rows do not protect every cross-row invariant.
- [ ] Perform assignment/revocation, `security_version` updates, and audit in
  the same transaction. Define the outcome when a client retries assignment
  or revocation of the same role.
- [ ] Test invalid FKs, duplicate pairs, retries, concurrent commands,
  self-escalation, and existing sessions after permission revocation.

Do not use cascading or broad mapper writes to bypass role-management use cases.

## identity_role_permission

**Purpose:** define which permissions are granted to each role.

- [ ] Create the association entity, PK `(role_id, permission_id)`, two FKs,
  timestamps, `version`, and an index on `permission_id`.
- [ ] Finalize an explicit role/permission matrix before seeding associations;
  do not assume every role or staff member receives all permissions.
- [ ] Write matrix-update use cases with authorization, concurrency control
  at the role level, and audit for grants/revocations.
- [ ] Choose how changes affect logged-in users: invalidate every affected
  identity or check current permissions at each protected use case. Increasing
  one user's version alone does not handle a role-wide change.
- [ ] Test duplicate pairs, invalid FKs, unauthorized actors, concurrent edits,
  and permission revocation across sessions on multiple instances.

## spring_session

**Purpose:** the login-session store managed by Spring Session JDBC.

- [ ] Add Boot-managed Spring Security and Spring Session JDBC dependencies
  following `LIBRARY.md`; verify the versions actually resolved.
- [ ] Take the PostgreSQL script from that exact Spring Session artifact and
  include it in a new Flyway migration. Preserve framework types/indexes; do
  not change all session time columns to `timestamptz` or session IDs to the
  business UUID type.
- [ ] Set `spring.session.jdbc.initialize-schema=never` so Flyway owns the schema.
- [ ] Configure principal/index mapping with a stable identity reference.
  Sessions hold only minimal data, authentication level, and the security
  version captured at authentication.
- [ ] Integrate login/logout through Spring Security, ensuring the context is
  saved correctly and the session ID changes after authentication. Do not
  invent a token format.
- [ ] Check current status/security version before protected access, alongside
  application authorization. Physical session deletion is only part of
  revocation; do not trust an old permission snapshot indefinitely.
- [ ] Configure HTTPS, appropriate `Secure`, `HttpOnly`, and `SameSite` cookie
  settings, CSRF, idle timeout, and server-side absolute-timeout checks under
  the agreed policy.
- [ ] Document the expired-session cleanup job's schedule and database load.
  Session-database failure must return a safe error, not grant access to
  protected endpoints.
- [ ] Test session ID rotation, missing/invalid CSRF, expiration, logout,
  revocation after credential/permission changes, multiple instances, and
  session-store failures.

## spring_session_attributes

**Purpose:** store session attributes through Spring Session.

- [ ] Use the framework schema, preserving the composite PK, session FK, and
  attribute deletion when the session is deleted. No custom JPA entity or
  repository is required for this table.
- [ ] Store only a minimal principal/context. Do not store passwords/hashes,
  entire JPA aggregates, financial information, or unnecessary customer data.
- [ ] Control serialized data types. Do not enable unsafe polymorphic
  deserialization or accept arbitrary serialized client data.
- [ ] Verify context round trips across instances and compatibility when
  deploying new application versions. Protect table access and backups
  according to their secret classification.

## audit_event

**Purpose:** shared evidence of sensitive actions, which must not be replaced
with operational diagnostic logs. An appropriate JDBC/JPA adapter may be used;
each module does not need its own audit entity/repository copy.

- [ ] Create a shared audit capability and a narrow `AuditEventPort`. Auth
  writes through that port, not another module's private repository.
- [ ] Create a migration based on the proposed DDL: event ID, UTC timestamp,
  actor, action, target, outcome, reason, correlation ID, and safe metadata
  as needed.
- [ ] Define a schema/allowlist for each event type's `details`. Do not dump
  requests, user entities, credentials, tokens, session IDs, or MFA secrets.
- [ ] Commit identity/credential/role changes and local audit in the same
  transaction. An audit failure must prevent the change from being reported
  as successful.
- [ ] Failed-login evidence needs a separate persistence boundary so it is not
  lost when rejected authentication rolls back. Failure to write mandatory
  audit evidence must have an explicit failure mode.
- [ ] Runtime audit writers have insert permission only, not table ownership
  or update/delete/truncate/DDL permissions. Read access and retention use
  controlled identities.
- [ ] Do not cascade-delete audit evidence when source data changes or is
  deleted. Retention and stronger evidence protection require an approved
  storage design; insert-only permissions do not prevent database
  administrators from modifying data.
- [ ] Test rollback on audit failure, persistence of failed-login evidence,
  redaction, database permissions, and event actor/target scope.

## Implementation Order

1. **Finalize the schema and migration history.** Review IDs, usernames, owner
   linkage, states, the permission matrix, the audit contract, and any `appuser`
   data.
2. **Build the identity foundation.** Complete user/credential migrations and
   mappings, domain code, ports/adapters, use cases, audit, and relevant tests
   before exposing mutation APIs.
3. **Build RBAC.** Implement role/permission catalogs, both association tables,
   assignment/revocation management, scope checks, and propagation of permission
   changes to sessions.
4. **Build login/session support.** Add the framework schema and Spring Security
   configuration, required MFA, CSRF, timeouts, revocation, abuse controls, and
   login/logout/me endpoints.
5. **Verify integration.** Test migrations on an isolated synthetic database,
   mappings, transactions, concurrency, database permissions, audit/session
   failures, and cross-customer/staff access.

Each step should cover migrations through use cases and meaningful tests.
Do not expose a sensitive endpoint merely because its entity or table exists.

## Mapping Status and Next Steps

Target mappings exist for user, credential, and login state; `createdAt` uses
`Instant`, no longer `LocalDate`. The custom session placeholder was removed;
Spring Session JDBC will manage both session tables. Offline PostgreSQL
metadata/DDL tests exist for the three new entities.

The next step is to reconcile Flyway history, create migrations appropriate
for the local schema, and verify them on an isolated synthetic PostgreSQL
database. V1-V7 source files were deleted in a previous commit; do not recreate
V1 or treat new-table DDL as a V6 upgrade migration. After the schema work,
connect the domain/application through ports, use cases, and audit.

## Work Still Required Beyond These Tables

- MFA enrollment/challenges, step-up, and recovery. Users who have not completed
  required MFA have restricted access only. Select storage/providers through
  a separate design.
- Failed-login tracking, temporary lockout, and rate limiting in shared state
  across instances. `identity_login_state` may be added when this mechanism
  is finalized.
- Activation/reset tokens: lifecycle, expiration, replay protection, and
  single-use enforcement.
- Idempotency/retries for provisioning and role-assignment commands, plus
  controlled operator bootstrapping. Do not introduce default credentials
  to solve bootstrap requirements.
- Verify current owner eligibility and resource-specific permissions in the
  application. Having a role does not imply access to every customer or
  permission to perform every transaction.

Policies without concrete values yet, including timeouts, lockout thresholds,
identities per owner, retention, and permission matrices, must be finalized
under repository/bank policy before dependent implementations are released.

## Verification Status

This checklist is documentation guidance. It does not implement security
controls, migrations, or financial logic. DDL, migration history, legacy data,
and runtime authentication must be verified during each implementation step.
Unchecked items do not indicate that the corresponding code is complete.

References: [SECURITY.md](../../SECURITY.md),
[LIBRARY.md](../../LIBRARY.md), [CODING_STANDARD.md](../../CODING_STANDARD.md),
[architecture](../ARCHITECTURE.md), and [proposed DDL](auth-session-schema-proposal.md).
