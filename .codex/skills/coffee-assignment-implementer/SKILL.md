---
name: coffee-assignment-implementer
description: coffee-order-service Spring Boot 과제 API를 구현하거나 수정할 때 사용한다. 커피 메뉴 목록 조회, 사용자 포인트 충전, 포인트 기반 커피 주문/결제, 주문 내역 Mock 실시간 전송, 최근 7일 인기 메뉴 TOP 3 조회를 다룬다. 이 저장소에서 과제 기능을 코딩하기 전에 사용한다.
---

# 커피 주문 과제 구현 스킬

## 먼저 읽을 문서

코딩 전에 현재 작업에 필요한 reference 문서만 읽는다.

- `references/requirements.md`: 과제 요구사항 체크리스트.
- `references/api-contract.md`: controller 또는 DTO를 추가/수정할 때.
- `references/domain-rules.md`: service, entity, repository 동작을 바꿀 때.
- `references/test-checklist.md`: 테스트를 추가하거나 구현을 마무리할 때.

## 구현 흐름

1. 계획
   - 사용자 요청이 어떤 과제 요구사항에 해당하는지 매핑한다.
   - 기존 패키지를 확인하고 현재 구조를 따른다.
   - 요구사항을 만족하는 가장 작은 구현 범위를 선택한다.

2. 생성
   - controller는 얇게 유지한다.
   - 포인트 충전, 결제, 주문 생성, 인기 메뉴 계산 규칙은 service에 둔다.
   - API 경계에는 DTO를 사용한다.
   - 금액과 포인트는 정수값으로 유지한다.
   - 주문 결제는 원자적으로 처리한다.
   - Mock 주문 이벤트는 주문 성공 후에만 전송한다.

3. 평가
   - 변경한 동작에 대한 집중 테스트를 실행한다.
   - 전체 Gradle 테스트를 실행한다.
   - 최종 응답 전에 과제 요구사항 목록을 다시 확인한다.

## 선호 아키텍처

기존 코드가 다른 구조를 명확히 선택하지 않았다면 아래 형태를 우선한다.

```text
com.example.coffeeorderservice
  menu
    MenuController
    MenuService
    MenuRepository
    CoffeeMenu
  point
    PointController
    PointService
    UserPoint
  order
    OrderController
    OrderService
    CoffeeOrder
    OrderEventClient
  common
    ApiExceptionHandler
    ErrorResponse
```

작은 과제에서는 in-memory repository와 H2/JPA 모두 가능하다.
다만 의존성 추가가 허용된다면 H2/JPA를 선호한다. 최근 7일 집계와 트랜잭션 경계를 보여주기 쉽기 때문이다.

## 완료 기준

- 모든 필수 API가 있고 request/response 형태가 안정적이다.
- 실패한 주문은 포인트를 차감하지 않는다.
- 실패한 주문은 Mock 이벤트를 전송하지 않는다.
- 성공한 주문은 포인트를 차감하고 Mock 이벤트를 1회 전송한다.
- 인기 메뉴는 최근 7일 내 성공 주문만 집계한다.
- 성공, 실패, 인기 메뉴 집계 테스트가 있다.
