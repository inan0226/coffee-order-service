# 커피 주문 서비스 에이전트 가이드

## 목표

이 프로젝트는 커피 주문 구현 과제를 작고 안정적인 Spring Boot API 서비스로 완성하는 것을 목표로 한다.
코드를 변경하기 전에 아래 과제 요구사항을 항상 보존한다.

- 커피 메뉴 목록을 메뉴 ID, 이름, 가격과 함께 조회한다.
- 사용자 식별값과 충전 금액을 받아 포인트를 충전한다. `1원 = 1P`로 계산한다.
- 사용자 식별값과 메뉴 ID를 받아 커피를 주문하고, 충전된 포인트에서 주문 금액을 차감한다.
- 성공한 주문 내역을 데이터 수집 플랫폼 Mock으로 실시간 전송한다.
- 최근 7일간 인기 있는 메뉴 3개를 주문 횟수 기준으로 정확하게 조회한다.

## 하네스 작업 흐름

모든 구현 작업은 아래 흐름을 따른다.

1. 계획
   - `.codex/skills` 아래의 관련 스킬 문서를 먼저 읽는다.
   - 요구사항을 구현 관점으로 다시 정리한다.
   - 영향을 받는 계층을 확인한다: controller, service, domain, repository, client, test.
   - 요구사항을 만족하는 가장 작은 변경 범위를 정한다.

2. 생성
   - 계획한 범위만 구현한다.
   - 비즈니스 규칙은 controller가 아니라 service 또는 domain 객체에 둔다.
   - API 경계에서는 request/response DTO를 사용한다.
   - 운영 코드와 테스트 코드를 같은 흐름에서 함께 추가하거나 수정한다.

3. 평가
   - 먼저 변경 범위에 맞는 집중 테스트를 실행한다.
   - 완료 선언 전 전체 테스트를 실행한다.
   - `.codex/skills/spring-assignment-reviewer/SKILL.md`의 요구사항 체크리스트를 확인한다.
   - 검증하지 못한 항목이 있으면 명확히 보고한다.

## 스킬 문서 사용 규칙

- 기능 구현 전에는 `.codex/skills/coffee-assignment-implementer/SKILL.md`를 읽는다.
- 테스트 설계 또는 테스트 누락 점검 전에는 `.codex/skills/spring-api-test-checker/SKILL.md`를 읽는다.
- 제출 전 최종 리뷰에는 `.codex/skills/spring-assignment-reviewer/SKILL.md`를 읽는다.

## 프로젝트 컨벤션

- 언어: Java 21.
- 프레임워크: Spring Boot.
- 기본 패키지: `com.example.coffeeorderservice`.
- 패키지 구조는 도메인 기준 또는 명확한 계층 기준으로 나눈다.
- 의존성 주입은 생성자 주입을 사용한다.
- 단순 request/response DTO는 `record`를 우선 사용한다.
- 금액과 포인트는 정수값으로 표현한다. `1원 = 1P`이므로 가능하면 `long`을 사용한다.
- controller에서 JPA Entity를 직접 노출하지 않는다.
- controller는 얇게 유지한다: 요청 형태 확인, service 호출, DTO 반환만 담당한다.
- 트랜잭션이 필요한 비즈니스 규칙은 service 메서드에 둔다.
- 주문 생성과 포인트 차감은 하나의 원자적인 작업이어야 한다.
- 실패한 주문은 포인트를 차감하지 않고 주문 이벤트도 전송하지 않는다.
- 성공한 주문은 포인트를 차감하고, 주문을 저장하고, 데이터 수집 플랫폼 Mock client를 호출한다.
- 최근 7일 같은 시간 기준 로직은 테스트 가능하도록 `Clock` 주입을 고려한다.

## API 컨벤션

- REST 스타일 endpoint와 JSON body를 사용한다.
- 응답은 안정적인 response DTO로 반환한다.
- HTTP 상태 코드는 명확하게 사용한다.
  - `200 OK`: 조회 성공, 포인트 충전 성공.
  - `201 Created`: 주문 생성 성공.
  - `400 Bad Request`: 잘못된 충전 금액, 포인트 부족 등.
  - `404 Not Found`: 존재하지 않는 사용자 또는 메뉴.
- 여러 API에서 같은 오류 응답이 필요하면 global exception handler를 추가한다.

## 테스트 컨벤션

- 핵심 비즈니스 규칙은 service 테스트를 우선한다.
- API request/response 형태가 중요하면 controller 테스트를 추가한다.
- 주문 이벤트 전송은 mock 또는 fake client로 검증한다.
- 최근 7일 경계값 테스트를 포함한다.
- 포인트 부족, 존재하지 않는 메뉴 같은 실패 테스트를 포함한다.

## Git 및 편집 안전 규칙

- 사용자가 명시적으로 요청하지 않는 한 사용자 변경사항을 되돌리지 않는다.
- 과제 구현과 무관한 리팩터링은 하지 않는다.
- 큰 변경 전에 현재 파일과 주변 패턴을 먼저 확인한다.
- 이미 프로젝트에 정해진 컨벤션이 있다면 이 문서보다 프로젝트 컨벤션을 우선한다.
