package com.example.coffeeorderservice.order;

/**
 * 주문과 포인트 결제가 성공했을 때 반환하는 JSON 응답입니다.
 *
 * @param orderId 생성된 주문 식별값
 * @param userId 주문한 사용자 식별값
 * @param menuId 주문한 메뉴 식별값
 * @param paidAmount 메뉴 가격만큼 차감한 포인트
 * @param remainingBalance 결제 후 남은 포인트 잔액
 */
public record OrderResponse(
		long orderId,
		long userId,
		long menuId,
		long paidAmount,
		long remainingBalance
) {
}
