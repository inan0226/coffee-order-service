package com.example.coffeeorderservice.order;

import java.time.Instant;
import java.util.List;

/**
 * 성공한 주문 기록을 저장하고 조회하는 저장소의 약속입니다.
 */
public interface OrderRepository {

	/**
	 * 결제가 끝난 주문을 저장합니다.
	 *
	 * @param userId 주문한 사용자 식별값
	 * @param menuId 주문한 메뉴 식별값
	 * @param paidAmount 결제 금액
	 * @param orderedAt 주문 성공 시각
	 * @return 식별값이 부여된 주문 기록
	 */
	CoffeeOrder save(long userId, long menuId, long paidAmount, Instant orderedAt);

	/**
	 * 저장된 전체 주문을 반환합니다.
	 *
	 * @return 성공한 모든 주문 기록
	 */
	List<CoffeeOrder> findAll();

	/**
	 * 지정한 시작과 종료 시각 사이에 성공한 주문을 찾습니다.
	 *
	 * @param startInclusive 포함할 시작 시각
	 * @param endInclusive 포함할 종료 시각
	 * @return 시간 범위에 포함되는 주문 기록
	 */
	List<CoffeeOrder> findByOrderedAtBetween(Instant startInclusive, Instant endInclusive);
}
