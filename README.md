# Coffee Order Service

포인트로 커피 메뉴를 주문하고, 주문 이벤트를 트랜잭션 아웃박스로 안전하게 전송하는 Spring Boot API입니다.

## 주요 기능

- 메뉴 목록과 최근 7일 인기 메뉴 TOP 3 조회
- 사용자 포인트 충전 및 잔액 기반 주문 결제
- 포인트 차감·주문 저장·아웃박스 저장의 단일 트랜잭션 처리
- 커밋 후 주문 이벤트 전송 및 실패 이벤트 재시도
- PostgreSQL/Flyway 기반 공유 저장소와 다중 인스턴스 동시성 고려

## 문서

- [API 명세](docs/API_SPEC.md)
- [ERD](docs/ERD.md)
- [주문 처리 흐름](docs/FLOWCHART.md)

## 기술 구성

- Java 21, Spring Boot 4.1
- Spring Web, Validation, Data JPA, JDBC
- PostgreSQL 16, Flyway
- Gradle, JUnit 5, AssertJ, Mockito

## 실행 방법

### 1. PostgreSQL 실행

```bash
docker compose up -d
```

기본 연결 정보는 `jdbc:postgresql://localhost:5432/coffee_order_service`이며, 사용자와 비밀번호는 모두 `coffee`입니다.
필요하면 아래 환경 변수로 변경할 수 있습니다.

| 환경 변수 | 기본값 | 설명 |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/coffee_order_service` | 데이터베이스 JDBC URL |
| `DB_USERNAME` | `coffee` | 데이터베이스 사용자 |
| `DB_PASSWORD` | `coffee` | 데이터베이스 비밀번호 |
| `SCHEDULER_POOL_SIZE` | `2` | 스케줄러 스레드 수 |
| `outbox.retry-delay-ms` | `1000` | 아웃박스 재시도 간격(ms) |

### 2. 애플리케이션 실행

```bash
./gradlew.bat bootRun
```

### 3. 테스트 실행

```bash
./gradlew.bat test
```

테스트는 PostgreSQL 호환 모드의 H2와 Flyway 마이그레이션을 사용합니다.

## 데이터 일관성 원칙

- 포인트 차감은 조건부 `UPDATE`로 수행해 잔액이 음수가 되지 않습니다.
- 주문 성공 시 포인트 차감, 주문, 아웃박스 이벤트가 함께 커밋됩니다.
- 이벤트 전송은 커밋 뒤 시작하며, 실패하면 아웃박스 이벤트를 재시도 상태로 돌립니다.
- 아웃박스는 at-least-once 전달을 전제로 합니다. 실제 외부 클라이언트를 연동할 때는 아웃박스 이벤트 ID를 멱등 키로 함께 전달해야 합니다.
