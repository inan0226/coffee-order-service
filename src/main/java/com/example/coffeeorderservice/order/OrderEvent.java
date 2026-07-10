package com.example.coffeeorderservice.order;

/**
 * 데이터 수집 플랫폼에 전송할 주문 요약 정보입니다.
 *
 * @param userId 주문한 사용자 식별값
 * @param menuId 주문한 메뉴 식별값
 * @param paidAmount 결제한 포인트 금액
 */
public record OrderEvent(long userId, long menuId, long paidAmount) {
}
