---
name: spring-release-checker
description: 이 coffee-order-service Spring Boot 프로젝트의 안전한 릴리스 전 점검을 수행한다. 릴리스 PR을 열기 전, 보호 브랜치에 병합하기 전, 배포 가능한 변경을 인계하기 전에 테스트, 마이그레이션·문서 존재 여부, git 공백 오류, 위험한 로컬 상태를 확인할 때 사용한다.
---

# Spring 릴리스 점검

## 절차

1. 저장소 루트에서 `scripts/release_preflight.py`를 실행한다. 이 스크립트는 필수 문서, Flyway 마이그레이션, `git diff --check`, 로컬 작업 트리 상태를 점검하고 기본으로 전체 Gradle 테스트를 실행한다.
2. 실패한 점검은 차단 요인으로 처리한다. 변경 사항이 남아 있는 작업 트리는 경고이므로 의도한 파일을 커밋하거나 관련 없는 변경을 정리한다.
3. 변경된 마이그레이션은 순방향 호환성을 검토한다. 이미 적용된 마이그레이션은 다시 작성하지 않는다.
4. 예상하지 못한 서버 오류가 기술 세부 정보를 노출하지 않는지, API 명세가 변경된 응답과 일치하는지 확인한다.
5. 후속 조치가 필요한 실패 명령은 `spring-diagnostic-journal`로 기록한다.

## 명령

```powershell
python .codex/skills/spring-release-checker/scripts/release_preflight.py
python .codex/skills/spring-release-checker/scripts/release_preflight.py --skip-tests
```

`--skip-tests`는 빠른 로컬 점검에만 사용하며 최종 릴리스 검증으로 사용해서는 안 된다.
