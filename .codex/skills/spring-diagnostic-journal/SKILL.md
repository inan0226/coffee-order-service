---
name: spring-diagnostic-journal
description: 이 coffee-order-service Spring Boot 프로젝트에서 재현 가능한 트러블슈팅 증적을 비식별화된 진단 저널에 남긴다. 테스트, 하네스, 시작 점검, API 요청, 데이터베이스 쿼리, 성능 조사 중 실패·의심되는 회귀·검증이 필요한 수정 사항을 발견했을 때 사용한다.
---

# Spring 진단 저널

## 절차

1. 가장 작은 관련 실패를 재현하거나 필요한 하네스·테스트를 실행한다.
2. 정확한 명령, 종료 코드, 증상, 의미 있는 로그 발췌를 수집한다. 비밀번호, 자격 증명, 액세스 토큰, 인가 헤더, 고객 데이터는 저널에 기록하지 않는다.
3. 저장소 루트에서 `scripts/record_incident.py`를 실행한다. 이 스크립트는 비식별화한 마크다운 항목을 `docs/troubleshooting/INCIDENTS.md`에 추가하고, 정제된 원문 출력을 `.codex/troubleshooting/logs/`에 기록한다.
4. 원인을 분석하거나 수정한 뒤 같은 이슈에 근본 원인, 변경 파일, 집중·전체 검증 결과를 갱신한다. 실패 증적을 통과 결과만 남기도록 덮어쓰지 않는다.
5. 이후 트러블슈팅 작업이 기존 증적에서 시작할 수 있도록 최종 보고서에 저널 항목을 언급한다.

## 실행 기록

```powershell
python .codex/skills/spring-diagnostic-journal/scripts/record_incident.py `
  --title "인기 메뉴 회귀" `
  --category test `
  --status resolved `
  --command ".\\gradlew.bat test --tests menu.PopularMenuServiceTest" `
  --exit-code 0 `
  --symptom "회귀 테스트가 통과함" `
  --root-cause "집계 결과 행마다 메뉴를 개별 조회함" `
  --change "집계 순서를 유지하면서 findAllById 사용" `
  --validation "집중 및 전체 Gradle 테스트 통과" `
  --log-file build/test-output.log
```

`--log-file`은 로컬에 보관해도 안전한 출력에만 사용한다. 스크립트가 일반적인 비밀 정보 형식을 가리고 저장 크기를 제한하더라도, 공유하거나 커밋하기 전에 직접 확인한다.

## 프로젝트 안전 규칙

- `docs/troubleshooting/INCIDENTS.md`는 간결하고 비식별화된 지속 증적이므로 커밋한다.
- `.codex/troubleshooting/logs/`는 비식별화 후에도 장황한 명령 출력이 포함될 수 있으므로 로컬에만 두고 무시 목록에 넣는다.
- 실패 시 재현 결과와 수정 뒤 검증 결과를 모두 기록한다.
- 비결정적인 동시성 또는 아웃박스 문제에는 시점, 시도·선점 식별자, 정확한 반복 테스트 명령을 포함한다.
- 원인 분석에는 `spring-troubleshooting`, 증적 기록에는 이 스킬, API 회귀 테스트 선정에는 `spring-assignment-reviewer`를 사용한다.
