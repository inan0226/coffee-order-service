---
name: spring-security-performance-reviewer
description: Review or harden this coffee-order-service Spring Boot application for security, reliability, and database performance. Use when reviewing changes, planning refactors, investigating slow endpoints, reducing query count, checking data exposure, or validating transaction and outbox safety.
---

# Spring Security and Performance Reviewer

## Review Order

1. Check security and data integrity first.
   - Validate request DTO constraints and avoid exposing entities or internal exception messages.
   - Keep SQL parameterized and avoid logging credentials, request secrets, or stack details in API responses.
   - Preserve transaction boundaries for point deduction, order creation, and outbox persistence.
   - Flag authentication or authorization work that would change the assignment API contract before implementing it.
2. Check database and runtime performance.
   - Look for N+1 repository calls, unbounded reads, missing deterministic ordering, and repeated aggregation.
   - Match high-frequency queries to existing indexes or propose a migration when an index is necessary.
   - Do not hold database locks while making external calls; claim outbox rows in a short transaction.
3. Add focused tests for each behavior change, then run the complete Gradle suite.

## Project Guardrails

- Return DTO records from controllers and keep business rules in services.
- Use `PageRequest` for bounded result sets and preserve aggregate ordering in application code.
- Keep the external order client behind `OrderEventClient`; do not make the in-memory mock a production dependency.
- Preserve the outbox's at-least-once semantics and document any receiver-side idempotency requirement.

## Findings Format

```text
[P1|P2|P3] File and line: issue
Impact:
Recommended change:
Test:
```
