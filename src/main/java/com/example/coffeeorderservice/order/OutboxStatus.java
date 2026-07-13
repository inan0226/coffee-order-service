package com.example.coffeeorderservice.order;

/**
 * 외부 데이터 수집 플랫폼 전송 이벤트의 처리 상태입니다.
 */
public enum OutboxStatus {
	PENDING,
	PROCESSING,
	SENT
}
