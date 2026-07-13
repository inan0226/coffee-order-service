---
name: spring-release-checker
description: Perform a safe pre-release check for this coffee-order-service Spring Boot project. Use before opening a release PR, merging to a protected branch, or handing off a deployable change to verify tests, migration and documentation presence, git whitespace errors, and potentially risky local state.
---

# Spring Release Checker

## Workflow

1. Run `scripts/release_preflight.py` from the repository root. It checks required docs, Flyway migrations, `git diff --check`, local worktree state, and runs the full Gradle suite by default.
2. Treat a failed check as a blocker. A dirty worktree is a warning: inspect it and either commit intended files or remove unrelated changes before release.
3. Review any changed migration for forward-only compatibility. Do not rewrite an already-applied migration.
4. Confirm that unexpected server errors do not expose technical details and that the API specification matches changed responses.
5. Use `spring-diagnostic-journal` for any failed command that needs follow-up.

## Commands

```powershell
python .codex/skills/spring-release-checker/scripts/release_preflight.py
python .codex/skills/spring-release-checker/scripts/release_preflight.py --skip-tests
```

`--skip-tests` is for a quick local inspection only and must not be the final release validation.
