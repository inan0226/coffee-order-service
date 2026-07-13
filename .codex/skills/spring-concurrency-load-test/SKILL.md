---
name: spring-concurrency-load-test
description: Validate concurrency-sensitive behavior in this coffee-order-service Spring Boot project. Use when changing point balance updates, order transactions, outbox claiming or retries, database locks, or when a test is flaky under repeated execution. Run the focused concurrency and consistency tests repeatedly before changing production code.
---

# Spring Concurrency Load Test

## Workflow

1. Choose the smallest affected test first:
   - point balance: `com.example.coffeeorderservice.point.PointConcurrencyIntegrationTest`
   - order and balance consistency: `com.example.coffeeorderservice.order.OrderConsistencyIntegrationTest`
   - outbox retry: `com.example.coffeeorderservice.order.OrderOutboxFailureIntegrationTest`
2. Repeat it with `scripts/run_repeated_test.py`. Increase runs only after a baseline passes.
3. On a failure, preserve the exact test class, run number, and output through `spring-diagnostic-journal` before modifying code.
4. Verify atomic SQL updates, transaction boundaries, and outbox claim-attempt ownership. Do not substitute JVM-local locks for database-level correctness.
5. Run the complete Gradle suite after a production-code change.

## Run a Repeated Check

```powershell
python .codex/skills/spring-concurrency-load-test/scripts/run_repeated_test.py --runs 10
python .codex/skills/spring-concurrency-load-test/scripts/run_repeated_test.py `
  --test com.example.coffeeorderservice.order.OrderConsistencyIntegrationTest --runs 5
```

The script stops at the first failed run and returns that Gradle exit code. It does not generate traffic against a live service.
