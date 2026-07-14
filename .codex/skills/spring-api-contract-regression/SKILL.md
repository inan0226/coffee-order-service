---
name: spring-api-contract-regression
description: 이 coffee-order-service Spring Boot 프로젝트에서 API 동작, DTO 검증, 오류 응답, 문서, 회귀 테스트를 일치시킨다. 컨트롤러 엔드포인트, 요청 또는 응답 레코드, HTTP 상태 코드, ErrorCode, 검증 규칙, 페이지네이션·정렬 동작, docs/API_SPEC.md를 추가하거나 변경할 때 사용한다.
---

# Spring API 계약 회귀 점검

## 점검 절차

1. 변경한 엔드포인트의 컨트롤러 매핑, 요청 DTO 제약 조건, 응답 DTO 필드, `docs/API_SPEC.md`를 비교한다.
2. 성공·실패 상태 코드, 응답 필드명, 오류 코드·메시지 동작, 목록 정렬의 결정성을 점검한다.
3. 필드 삭제·이름 변경, 상태 코드 변경, 검증 강화는 호환성 변경으로 간주하고 구현 전에 알린다.
4. 가장 작은 API 회귀 테스트를 추가하거나 갱신한다. 입력 처리를 변경했다면 잘못된 JSON 또는 빈 검증도 확인한다.
5. 집중 API 테스트와 전체 Gradle 테스트를 차례로 실행한다. 예상 밖 결과는 `spring-diagnostic-journal`로 기록한다.

## 프로젝트 안전 규칙

- 컨트롤러에서는 JPA 엔티티가 아닌 응답 레코드를 반환한다.
- 예상하지 못한 예외에 대한 `ApiExceptionHandler` 응답은 일반적인 내용으로 유지한다.
- 외부에 노출되는 오류 코드는 `docs/API_SPEC.md`에 문서화한다.
- 클라이언트가 관찰할 수 있는 컬렉션 조회는 건수를 제한하고 정렬 기준을 문서화한다.
