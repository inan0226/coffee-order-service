---
name: spring-troubleshooting
description: Diagnose and explain failures in this coffee-order-service Spring Boot project. Use when startup, Flyway, PostgreSQL, API validation, transactions, point balances, outbox delivery, concurrency, or tests fail or behave unexpectedly. Gather reproducible evidence before proposing a fix.
---

# Spring Troubleshooting

## Workflow

1. Reproduce the symptom with the smallest relevant Gradle test, API request, log, or database query.
2. Classify the failure before editing:
   - startup/configuration: profile, environment variables, Docker Compose, Flyway, JPA validation
   - API: request DTO validation, controller mapping, `ApiExceptionHandler`, response contract
   - data/transaction: transaction boundary, SQL row count, rollback, schema/index mismatch
   - concurrency/outbox: conditional point update, claim status, lease timeout, retry ownership
3. Inspect only the affected controller, service, repository, migration, and test.
4. State the root cause with evidence, then make the smallest safe change if implementation was requested.
5. Run the focused regression test and the full test suite after a fix.

## Project Checks

- Confirm PostgreSQL settings in `application.properties` and the H2 test profile in `src/test/resources`.
- Treat `user_points` updates as atomic SQL operations; do not replace them with read-modify-write logic.
- Keep point deduction, order persistence, and outbox persistence in the same transaction.
- Treat the outbox as at-least-once delivery. Check claim attempt ownership before changing `PROCESSING` events.
- Use the `Clock` bean for time-window and timeout tests.

## Report Format

```text
Symptom:
Root cause:
Evidence:
Fix or next diagnostic step:
Validation:
```
