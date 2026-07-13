---
name: spring-diagnostic-journal
description: Capture reproducible troubleshooting evidence for this coffee-order-service Spring Boot project in a durable, redacted diagnostic journal. Use when running tests, a harness, startup checks, API requests, database queries, or performance investigations that expose a failure, a suspected regression, or a fix that needs verification.
---

# Spring Diagnostic Journal

## Workflow

1. Reproduce the smallest relevant failure or run the required harness/test.
2. Capture the exact command, exit code, symptom, and meaningful log excerpt. Do not write secrets, credentials, access tokens, authorization headers, or customer data to the journal.
3. Run `scripts/record_incident.py` from the repository root. It appends a redacted Markdown entry to `docs/troubleshooting/INCIDENTS.md` and writes the sanitized raw output to `.codex/troubleshooting/logs/`.
4. After diagnosing or fixing, update the same incident with the root cause, changed files, and focused/full validation results. Preserve the failed evidence; do not overwrite it with only the passing run.
5. Refer to the journal entry in the final report so a later troubleshooting task can begin from existing evidence.

## Record a Run

```powershell
python .codex/skills/spring-diagnostic-journal/scripts/record_incident.py `
  --title "Popular-menu regression" `
  --category test `
  --status resolved `
  --command ".\\gradlew.bat test --tests menu.PopularMenuServiceTest" `
  --exit-code 0 `
  --symptom "Regression test now passes" `
  --root-cause "Menu lookup performed one query per aggregate row" `
  --change "Use findAllById while preserving aggregate order" `
  --validation "Focused and full Gradle tests passed" `
  --log-file build/test-output.log
```

Use `--log-file` only for output that is safe to retain locally. The script redacts common secret formats and limits the saved excerpt; inspect it before sharing or committing.

## Project Guardrails

- Keep `docs/troubleshooting/INCIDENTS.md` committed: it contains concise, redacted, durable evidence.
- Keep `.codex/troubleshooting/logs/` local and ignored: it may contain noisy command output even after redaction.
- For failures, record both the reproduction result and the validation after a fix.
- For nondeterministic concurrency or outbox issues, include timing, attempt/claim identifiers, and the exact test repetition command.
- Use `spring-troubleshooting` for diagnosis and this skill for evidence capture; use `spring-api-test-checker` to select API regression tests.
