package com.example.coffeeorderservice.order;

import com.example.coffeeorderservice.menu.CoffeeMenu;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴 주문, 포인트 결제, 주문 저장, 이벤트 전송을 순서대로 처리하는 Service입니다.
 *
 * <p>포인트 차감, 주문 저장, 아웃박스 저장을 하나의 DB 트랜잭션으로 처리합니다.
 * 각 서버 인스턴스가 같은 DB를 사용하므로 JVM 잠금 없이도 데이터 일관성을 지킬 수 있습니다.</p>
 */
@Service
public class OrderService {

	private final MenuService menuService;
	private final PointService pointService;
	private final CoffeeOrderJpaRepository coffeeOrderJpaRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final Clock clock;
	private final ApplicationEventPublisher eventPublisher;

	public OrderService(
			MenuService menuService,
			PointService pointService,
			CoffeeOrderJpaRepository coffeeOrderJpaRepository,
			OutboxEventRepository outboxEventRepository,
			Clock clock,
			ApplicationEventPublisher eventPublisher
	) {
		this.menuService = menuService;
		this.pointService = pointService;
		this.coffeeOrderJpaRepository = coffeeOrderJpaRepository;
		this.outboxEventRepository = outboxEventRepository;
		this.clock = clock;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * 사용자 포인트로 메뉴를 주문합니다.
	 *
	 * <ol>
	 *   <li>메뉴가 존재하는지 확인합니다.</li>
	 *   <li>메뉴 가격만큼 포인트를 차감합니다.</li>
	 *   <li>성공 주문을 저장합니다.</li>
	 *   <li>주문 이벤트를 아웃박스에 함께 저장합니다.</li>
	 * </ol>
	 *
	 * <p>메뉴가 없거나 포인트가 부족하면 트랜잭션 전체가 롤백되어 주문과 이벤트가 남지 않습니다.
	 * 커밋 뒤 외부 전송에 실패해도 아웃박스 이벤트가 남아 다른 인스턴스가 재시도합니다.</p>
	 *
	 * @param userId 주문할 사용자 식별값
	 * @param menuId 주문할 메뉴 식별값
	 * @return 생성된 주문 정보와 결제 후 잔액
	 */
	@Transactional
	public OrderResponse order(long userId, long menuId) {
		CoffeeMenu menu = menuService.getMenu(menuId);
		long remainingBalance = pointService.deduct(userId, menu.price());
		CoffeeOrderEntity order = coffeeOrderJpaRepository.save(
				new CoffeeOrderEntity(userId, menu.id(), menu.price(), clock.instant())
		);
		outboxEventRepository.save(new OutboxEvent(userId, menu.id(), menu.price(), clock.instant()));
		eventPublisher.publishEvent(new OrderCommittedEvent());

		return new OrderResponse(order.id(), userId, menu.id(), menu.price(), remainingBalance);
	}
}
