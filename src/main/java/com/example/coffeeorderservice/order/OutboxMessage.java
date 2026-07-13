package com.example.coffeeorderservice.order;

/**
 * DB 잠금을 해제한 뒤 외부 플랫폼에 보낼 아웃박스 이벤트의 읽기 전용 데이터입니다.
 *
 * @param outboxEventId 전송 상태를 갱신할 아웃박스 이벤트 ID
 * @param claimAttempt 이 메시지를 확보한 처리 시도의 번호
 * @param orderEvent 외부 플랫폼에 전송할 주문 데이터
 */
public record OutboxMessage(long outboxEventId, int claimAttempt, OrderEvent orderEvent) {
}
