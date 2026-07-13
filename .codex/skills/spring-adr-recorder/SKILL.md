---
name: spring-adr-recorder
description: Record durable architecture decisions for this coffee-order-service Spring Boot project in concise Korean ADR documents. Use when selecting or changing a persistence strategy, transaction boundary, outbox delivery model, concurrency control, API compatibility policy, observability approach, or another decision with meaningful alternatives and long-term consequences.
---

# Spring ADR Recorder

## Workflow

1. Create an ADR only for a durable decision with alternatives; do not create one for a mechanical refactor.
2. Store it under `docs/adr/` as the next zero-padded sequence number and a kebab-case title, for example `0001-outbox-delivery.md`.
3. Write the ADR in Korean using this structure:

```markdown
# ADR-0001: Decision title

## Status
Accepted

## Context

## Decision

## Considered alternatives

## Consequences and trade-offs
```

4. State why the selected option fits the current service, what it does not solve, and which code, migration, API document, or test enforces it.
5. Link the ADR from related documentation only when it materially improves discoverability.

## Existing Project Decisions to Preserve

- Point balance updates rely on conditional SQL, not JVM-local locking.
- Order persistence, point deduction, and outbox persistence share a transaction.
- Outbox delivery is at-least-once; consumers require idempotency.
- Popular-menu results are bounded and deterministically ordered.
