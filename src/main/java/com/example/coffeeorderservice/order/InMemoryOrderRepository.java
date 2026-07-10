package com.example.coffeeorderservice.order;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * 성공한 주문을 서버 메모리에 저장하는 저장소입니다.
 *
 * <p>주문 ID는 AtomicLong으로 순서대로 발급하고, 주문 목록은 여러 요청이 동시에 조회해도
 * 안전하게 읽을 수 있도록 CopyOnWriteArrayList에 보관합니다.</p>
 */
@Repository
public class InMemoryOrderRepository implements OrderRepository {

	private final AtomicLong sequence = new AtomicLong();
	private final List<CoffeeOrder> orders = new CopyOnWriteArrayList<>();

	/**
	 * 새 주문 ID를 발급해 주문 기록을 메모리에 추가합니다.
	 *
	 * @param userId 주문한 사용자 식별값
	 * @param menuId 주문한 메뉴 식별값
	 * @param paidAmount 결제 금액
	 * @param orderedAt 주문 시각
	 * @return 저장된 주문 기록
	 */
	@Override
	public CoffeeOrder save(long userId, long menuId, long paidAmount, Instant orderedAt) {
		CoffeeOrder order = new CoffeeOrder(sequence.incrementAndGet(), userId, menuId, paidAmount, orderedAt);
		orders.add(order);
		return order;
	}

	/**
	 * 현재 메모리에 있는 전체 주문을 복사해서 반환합니다.
	 *
	 * @return 전체 성공 주문 목록
	 */
	@Override
	public List<CoffeeOrder> findAll() {
		return List.copyOf(orders);
	}

	/**
	 * 시작과 종료 시각을 모두 포함해 주문을 조회합니다.
	 *
	 * @param startInclusive 포함할 시작 시각
	 * @param endInclusive 포함할 종료 시각
	 * @return 시간 범위 안의 주문 목록
	 */
	@Override
	public List<CoffeeOrder> findByOrderedAtBetween(Instant startInclusive, Instant endInclusive) {
		return orders.stream()
				.filter(order -> !order.orderedAt().isBefore(startInclusive))
				.filter(order -> !order.orderedAt().isAfter(endInclusive))
				.toList();
	}
}
