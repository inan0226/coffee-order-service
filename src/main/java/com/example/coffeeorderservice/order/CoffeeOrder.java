package com.example.coffeeorderservice.order;

import java.time.Instant;

/**
 * 포인트 결제에 성공한 커피 주문 한 건을 표현하는 데이터입니다.
 *
 * <p>주문이 실패하면 이 객체는 만들어지지 않으므로, 인기 메뉴 집계에는 이 주문 기록만 사용합니다.</p>
 *
 * @param id 주문 식별값
 * @param userId 주문한 사용자 식별값
 * @param menuId 주문한 메뉴 식별값
 * @param paidAmount 실제로 차감한 포인트
 * @param orderedAt 주문이 성공한 시각
 */
public record CoffeeOrder(
		long id,
		long userId,
		long menuId,
		long paidAmount,
		Instant orderedAt
) {
}
