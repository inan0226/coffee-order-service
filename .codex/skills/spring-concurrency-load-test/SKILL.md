---
name: spring-concurrency-load-test
description: 이 coffee-order-service Spring Boot 프로젝트에서 동시성에 민감한 동작을 검증한다. 포인트 잔액 갱신, 주문 트랜잭션, 아웃박스 선점·재시도, 데이터베이스 잠금을 변경하거나 반복 실행에서 테스트가 불안정할 때 사용한다. 운영 코드를 변경하기 전에 관련 동시성·일관성 테스트를 반복 실행한다.
---

# Spring 동시성 반복 테스트

## 절차

1. 영향을 받는 가장 작은 테스트를 먼저 선택한다.
   - 포인트 잔액: `com.example.coffeeorderservice.point.PointConcurrencyIntegrationTest`
   - 주문과 잔액 일관성: `com.example.coffeeorderservice.order.OrderConsistencyIntegrationTest`
   - 아웃박스 재시도: `com.example.coffeeorderservice.order.OrderOutboxFailureIntegrationTest`
2. `scripts/run_repeated_test.py`로 테스트를 반복한다. 기준 실행이 통과한 뒤에만 반복 횟수를 늘린다.
3. 실패 시 코드를 수정하기 전에 정확한 테스트 클래스, 실행 횟수, 출력을 `spring-diagnostic-journal`로 보존한다.
4. 원자적 SQL 갱신, 트랜잭션 경계, 아웃박스 선점 시도의 소유권을 검증한다. JVM 로컬 락으로 데이터베이스 수준의 정확성을 대체하지 않는다.
5. 운영 코드를 변경했다면 전체 Gradle 테스트를 실행한다.

## 반복 검증 실행

```powershell
python .codex/skills/spring-concurrency-load-test/scripts/run_repeated_test.py --runs 10
python .codex/skills/spring-concurrency-load-test/scripts/run_repeated_test.py `
  --test com.example.coffeeorderservice.order.OrderConsistencyIntegrationTest --runs 5
```

스크립트는 첫 실패에서 중단하고 해당 Gradle 종료 코드를 반환한다. 실행 중인 서비스에 트래픽을 발생시키지 않는다.
