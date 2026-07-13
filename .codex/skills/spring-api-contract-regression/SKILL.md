---
name: spring-api-contract-regression
description: Keep API behavior, DTO validation, error responses, documentation, and regression tests aligned in this coffee-order-service Spring Boot project. Use when adding or changing a controller endpoint, request or response record, HTTP status, ErrorCode, validation rule, pagination or ordering behavior, or docs/API_SPEC.md.
---

# Spring API Contract Regression

## Review Workflow

1. Compare the controller mapping, request DTO constraints, response DTO fields, and `docs/API_SPEC.md` for the changed endpoint.
2. Check success and failure status codes, response field names, error code/message behavior, and deterministic list ordering.
3. Treat removal or renaming of a field, status-code change, or validation tightening as a compatibility change; call it out before implementation.
4. Add or update the smallest API regression test. Cover malformed JSON or bean validation when input handling changes.
5. Run the focused API test and then the full Gradle suite. Capture an unexpected result through `spring-diagnostic-journal`.

## Project Guardrails

- Return response records rather than JPA entities from controllers.
- Keep `ApiExceptionHandler` responses generic for unexpected exceptions.
- Document externally visible error codes in `docs/API_SPEC.md`.
- Bound collection queries and document their ordering when clients can observe it.
