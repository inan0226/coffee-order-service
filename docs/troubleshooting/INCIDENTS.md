# Troubleshooting Journal

Redacted records of reproducible failures and their validation.


## 2026-07-13 18:49 +0900 — Diagnostic journal setup verification

- Status: `resolved`
- Category: `test`
- Command: `.\\gradlew.bat test --rerun-tasks`
- Exit code: `0`
- Symptom: Regression suite required a durable troubleshooting record
- Root cause: No project-local workflow existed to retain reproducible diagnosis evidence
- Change: Added redacted diagnostic journal skill and recorder
- Validation: Full Gradle test suite passed before this record was created
- Local log excerpt: `-`


## 2026-07-13 18:49 +0900 — Full test report capture verification

- Status: `resolved`
- Category: `test`
- Command: `.\\gradlew.bat test --rerun-tasks`
- Exit code: `0`
- Symptom: Test evidence must remain available to later troubleshooting runs
- Root cause: -
- Change: -
- Validation: Recorder stored a redacted local report excerpt
- Local log excerpt: `.codex/troubleshooting/logs/20260713-184948.log`


## 2026-07-13 18:57 +0900 - Gradle 집중 테스트 필터 정정

- Status: `resolved`
- Category: `test`
- Command: `.\\gradlew.bat test --tests com.example.coffeeorderservice.common.ApiExceptionHandlerTest --tests com.example.coffeeorderservice.menu.PopularMenuServiceTest --rerun-tasks`
- Exit code: `0`
- Symptom: 축약된 테스트 클래스명으로 실행하면 Gradle이 테스트를 찾지 못함
- Root cause: Gradle --tests 필터에는 완전한 패키지명을 사용해야 함
- Change: 완전한 패키지명으로 테스트 선택자를 정정
- Validation: 정정한 집중 테스트와 전체 Gradle 테스트가 통과함
- Local log excerpt: `-`
