---
name: spring-flyway-migration-reviewer
description: Review or plan safe Flyway schema and index changes for this coffee-order-service Spring Boot project. Use when adding a migration, changing a table or column, adding an index, diagnosing query performance, or checking PostgreSQL and H2 migration compatibility.
---

# Spring Flyway Migration Reviewer

## Review Workflow

1. Read the affected repository query and entity mapping before designing a migration.
2. Add a new forward-only `V<version>__<description>.sql` migration. Do not edit a migration that may already have been applied.
3. Evaluate nullability, defaults, existing-row backfill, unique constraints, and index write cost. Flag destructive DDL or data loss for explicit approval.
4. Match indexes to actual filter, join, grouping, and ordering columns; avoid speculative indexes.
5. Validate startup and integration tests with the H2 test profile, then confirm PostgreSQL-specific syntax is intentional and documented.
6. Update entity mappings, repository tests, and ERD/API documentation when the externally relevant schema changes.

## Project Guardrails

- Preserve the atomic `user_points` update semantics.
- Keep outbox state and claim-attempt columns compatible with retry ownership checks.
- Never put production credentials or captured production rows into a migration or test fixture.
