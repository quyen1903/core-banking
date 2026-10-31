# Decision: Bouncy Castle runtime support for Argon2

Date: 2026-10-30. Scope: local development dependency repair.
Risk: R3 application security; L4 dependency under `LIBRARY.md`.

## Context and decision

The existing `SpringPasswordHashingAdapter` delegates to Spring Security's
`Argon2PasswordEncoder`. Registration fails with `NoClassDefFoundError` for
`org.bouncycastle.crypto.params.Argon2Parameters$Builder` because the provider
is absent at runtime. Spring documents Bouncy Castle as a required dependency
for this encoder. No custom hashing implementation or algorithm change is needed.

Declare `org.bouncycastle:bcprov-jdk18on:1.86` as `runtimeOnly`. Application code
imports Spring's encoder API, so the provider is needed at runtime rather than
as an application compile dependency. The cached Spring Boot 4.1.0 dependency
BOM does not manage this artifact; the version is explicitly pinned, with no
Boot-managed version override. Use the existing Maven Central repository.

The vendor publishes 1.86 as a stable release and documents security fixes in
its release notes. Only the core provider artifact is added; no OpenPGP, PKIX,
native library, remote service, or global JCA provider registration is added.
This repairs the runtime of the already selected encoder; it does not approve
password policy, cost parameters, or production deployment.

## Data flow and failure behavior

The application passes the password through `PasswordHashingPort` to Spring's
encoder. The Argon2 calculation uses Bouncy Castle in process, and the existing
credential flow receives the complete encoded hash. No new network destination,
telemetry integration, or password logging is introduced by this change.

Provider failures must propagate through the registration transaction. There is
no plaintext fallback or success response after hashing fails. Passwords and
hashes must remain excluded from public responses, logs, and audit payloads.

## Review and remaining verification

The vendor license is MIT-style; retain the artifact's license and notices.
Vendor release notes and the Spring dependency requirement were reviewed.
This is an engineering dependency decision, not formal legal or security approval.

Dependency resolution, the complete resolved dependency tree, artifact integrity,
automated vulnerability/license scanning, runtime compatibility with Java 25,
and positive/negative credential verification remain unverified. No build or
tests were run, following the user's development instruction. Complete the
required governance and verification before use in a shared environment.

If the encoder or provider changes later, review stored-hash compatibility and
credential migration first; do not remove the provider while existing flows
still require it.

## Sources

- [Spring Security password storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-argon2)
- [Bouncy Castle provider API](https://downloads.bouncycastle.org/java/docs/bcprov-jdk18on-javadoc/org/bouncycastle/crypto/params/Argon2Parameters.Builder.html)
- [Bouncy Castle Java 1.86 release](https://www.bouncycastle.org/resources/new-release-bouncy-castle-java-1-86/)
- [Bouncy Castle license](https://www.bouncycastle.org/about/license/)
