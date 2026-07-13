---
name: spring-observability-reviewer
description: Review and improve observability for this coffee-order-service Spring Boot application. Use when adding logs, diagnosing an API or outbox failure, reviewing error handling, deciding which metrics or correlation identifiers to capture, or checking that troubleshooting evidence is useful without exposing sensitive data.
---

# Spring Observability Reviewer

## Review Workflow

1. Identify the operation boundary: HTTP request, point update, order transaction, outbox claim, or external event delivery.
2. Check that failures can be correlated with stable, non-secret identifiers such as order ID, outbox event ID, claim attempt, and request ID when available.
3. Keep API responses generic for unexpected errors; place technical stack details only in server logs.
4. Never log passwords, tokens, authorization headers, JDBC credentials, or full customer payloads. Log IDs and result states instead.
5. For retries, log the event ID, attempt count, state transition, and exception type. Do not claim success before the state update succeeds.
6. Capture a redacted command result with `spring-diagnostic-journal` whenever a reproducible failure is found.

## Signals to Prefer

- Order flow: user ID, menu ID, order ID, paid amount, remaining balance, transaction result.
- Outbox flow: outbox event ID, claim attempt, status before/after, retry result.
- Performance: endpoint or repository operation, query count regression, bounded result size, elapsed time only when it helps compare a baseline.

## Guardrails

- Do not add an observability platform, external telemetry SDK, or request-header contract without confirming the operational choice.
- Do not replace structured domain errors with raw exception messages.
- Keep logs concise enough to be actionable; prefer one state-transition log over duplicate logs at every layer.
