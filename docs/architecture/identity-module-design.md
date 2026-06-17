# Identity Module Design

## Meaning

`IdentityUser` is the login-capable security identity for an existing QuinnBank
business owner. It is not a bank account and it does not hold money, balances,
ledger state, account numbers, or account lifecycle.

`IdentityUser` belongs to one owner:

- `CUSTOMER`
- `STAFF`

The identity module stores the security-facing username, optional contact
lookup fields, status, credentials, and role assignments. The public API exposes
`publicId` and does not expose the internal numeric primary key.

## Bank Account Versus IdentityUser

In QuinnBank, `Account` means bank account. Bank accounts live in the account
module and represent deposit-account lifecycle and financial state.

`IdentityUser` is a security principal. It is used for authentication and
authorization foundations. Naming login identities as accounts creates ambiguity
with bank accounts and must be avoided in API, domain, and persistence code.

## Why Login Is Deferred

`/api/v1/auth/login` cannot be implemented safely before the identity foundation
exists because login needs:

- an `IdentityUser` with clear owner linkage and status;
- an encoded credential record separate from the user profile;
- role and permission mapping for authorities;
- future token, refresh-token, failed-attempt, and lockout controls.

This slice creates the storage and use cases needed before login is introduced.

## Main Tables

- `identity_user`: internal id, public UUID, owner type/id, username, optional
  email and phone number, status, audit timestamps, and optimistic version.
- `identity_credential`: password credential hash for an identity user. It
  stores only encoded password hashes.
- `identity_role`: supported role codes: `CUSTOMER`, `TELLER`, `ADMIN`.
- `identity_permission`: supported permission codes.
- `identity_user_role`: user-role assignment join table.
- `identity_role_permission`: role-permission mapping join table.

## Main APIs

- `POST /api/v1/identity/users`
- `GET /api/v1/identity/users/{publicId}`
- `PATCH /api/v1/identity/users/{publicId}/status`
- `POST /api/v1/identity/users/{publicId}/credentials/password`
- `POST /api/v1/identity/users/{publicId}/roles`
- `DELETE /api/v1/identity/users/{publicId}/roles/{roleCode}`

## Future Work

- activation token flow;
- `POST /api/v1/auth/login`;
- `POST /api/v1/auth/refresh`;
- `POST /api/v1/auth/logout`;
- password reset;
- JWT access and refresh token storage;
- failed login attempts and lockout.
