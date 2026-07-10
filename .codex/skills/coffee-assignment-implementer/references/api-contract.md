# API 계약 가이드

사용자가 다른 endpoint 이름을 요구하지 않으면 아래 형태를 기본으로 사용한다.

## 메뉴 목록

```http
GET /api/menus
```

응답:

```json
[
  {
    "menuId": 1,
    "name": "Americano",
    "price": 4500
  }
]
```

## 포인트 충전

```http
POST /api/points/charge
Content-Type: application/json
```

요청:

```json
{
  "userId": 1,
  "amount": 10000
}
```

응답:

```json
{
  "userId": 1,
  "balance": 10000
}
```

## 주문

```http
POST /api/orders
Content-Type: application/json
```

요청:

```json
{
  "userId": 1,
  "menuId": 1
}
```

응답:

```json
{
  "orderId": 1,
  "userId": 1,
  "menuId": 1,
  "paidAmount": 4500,
  "remainingBalance": 5500
}
```

## 인기 메뉴

```http
GET /api/menus/popular
```

응답:

```json
[
  {
    "menuId": 1,
    "name": "Americano",
    "price": 4500,
    "orderCount": 10
  }
]
```

## 오류 응답

오류 응답은 아래처럼 단순하고 일관된 형태를 사용한다.

```json
{
  "code": "INSUFFICIENT_POINTS",
  "message": "포인트 잔액이 부족합니다."
}
```
