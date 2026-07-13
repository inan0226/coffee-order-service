# API 명세

기본 경로는 로컬 실행 기준 `http://localhost:8080`입니다. 모든 요청과 응답은 JSON을 사용합니다.

## 공통 오류 응답

업무 오류와 요청 검증 실패는 아래 형식으로 응답합니다. 처리되지 않은 내부 예외도 동일한 형식을 사용하지만, 기술 상세나 스택 트레이스는 응답에 포함하지 않습니다.

```json
{
  "code": "INSUFFICIENT_POINTS",
  "message": "포인트 잔액이 부족합니다."
}
```

| 코드 | HTTP 상태 | 의미 |
| --- | --- | --- |
| `INVALID_REQUEST` | 400 | JSON 형식 또는 요청 필드가 올바르지 않음 |
| `POINT_BALANCE_OVERFLOW` | 400 | 포인트 잔액이 `long` 범위를 초과함 |
| `INSUFFICIENT_POINTS` | 400 | 주문에 필요한 포인트가 부족함 |
| `USER_NOT_FOUND` | 404 | 포인트 정보가 없는 사용자임 |
| `MENU_NOT_FOUND` | 404 | 존재하지 않는 메뉴임 |
| `INTERNAL_SERVER_ERROR` | 500 | 내부 예외 발생 시 상세 정보를 노출하지 않는 공통 오류 |

`userId`, `menuId`, `amount`가 누락되었거나 1 미만이면 Bean Validation 단계에서 `INVALID_REQUEST`(400)를 반환합니다.

## 메뉴 목록 조회

`GET /api/menus`

메뉴 ID 오름차순으로 판매 메뉴를 반환합니다.

### 성공 응답 — 200 OK

```json
[
  {
    "menuId": 1,
    "name": "Americano",
    "price": 4500
  }
]
```

## 인기 메뉴 조회

`GET /api/menus/popular`

현재 시각부터 최근 7일(시작·종료 시각 포함)의 성공 주문을 집계해 최대 3개를 반환합니다. 주문 수 내림차순, 메뉴 ID 오름차순으로 정렬합니다. 집계한 메뉴 ID는 한 번의 일괄 메뉴 조회로 결합하므로 반환 순서는 집계 순서를 유지합니다.

### 성공 응답 — 200 OK

```json
[
  {
    "menuId": 1,
    "name": "Americano",
    "price": 4500,
    "orderCount": 12
  }
]
```

## 포인트 충전

`POST /api/points/charge`

### 요청

```json
{
  "userId": 1,
  "amount": 10000
}
```

| 필드 | 타입 | 제약 |
| --- | --- | --- |
| `userId` | number | 1 이상 |
| `amount` | number | 1 이상 |

### 성공 응답 — 200 OK

```json
{
  "userId": 1,
  "balance": 10000
}
```

## 주문 생성

`POST /api/orders`

### 요청

```json
{
  "userId": 1,
  "menuId": 1
}
```

| 필드 | 타입 | 제약 |
| --- | --- | --- |
| `userId` | number | 1 이상, 포인트 정보 필요 |
| `menuId` | number | 1 이상, 존재하는 메뉴 필요 |

### 성공 응답 — 201 Created

```json
{
  "orderId": 1,
  "userId": 1,
  "menuId": 1,
  "paidAmount": 4500,
  "remainingBalance": 5500
}
```

주문 처리 중 포인트 차감, 주문 저장, 아웃박스 이벤트 저장은 하나의 트랜잭션으로 처리됩니다. 주문과 아웃박스 이벤트는 같은 업무 시각을 기록합니다. 이벤트 외부 전송 실패는 주문 실패가 아니라 아웃박스 재시도로 처리됩니다.
