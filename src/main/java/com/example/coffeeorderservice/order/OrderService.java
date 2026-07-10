package com.example.coffeeorderservice.order;

import com.example.coffeeorderservice.menu.CoffeeMenu;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import org.springframework.stereotype.Service;

/**
 * 메뉴 주문, 포인트 결제, 주문 저장, 이벤트 전송을 순서대로 처리하는 Service입니다.
 *
 * <p>주문 처리 전체를 동기화해 동시에 여러 주문이 들어와도 한 주문의 결제 단계가
 * 중간에 다른 주문과 섞이지 않게 합니다.</p>
 */
@Service
public class OrderService {

	private final MenuService menuService;
	private final PointService pointService;
	private final OrderRepository orderRepository;
	private final OrderEventClient orderEventClient;
	private final Clock clock;

	public OrderService(
			MenuService menuService,
			PointService pointService,
			OrderRepository orderRepository,
			OrderEventClient orderEventClient,
			Clock clock
	) {
		this.menuService = menuService;
		this.pointService = pointService;
		this.orderRepository = orderRepository;
		this.orderEventClient = orderEventClient;
		this.clock = clock;
	}

	/**
	 * 사용자 포인트로 메뉴를 주문합니다.
	 *
	 * <ol>
	 *   <li>메뉴가 존재하는지 확인합니다.</li>
	 *   <li>메뉴 가격만큼 포인트를 차감합니다.</li>
	 *   <li>성공 주문을 저장합니다.</li>
	 *   <li>데이터 수집 플랫폼 Mock에 주문 이벤트를 즉시 전송합니다.</li>
	 * </ol>
	 *
	 * <p>메뉴가 없거나 포인트가 부족하면 두 번째 단계 이전 또는 도중에 예외가 발생하며,
	 * 주문 기록과 이벤트는 남지 않습니다.</p>
	 *
	 * @param userId 주문할 사용자 식별값
	 * @param menuId 주문할 메뉴 식별값
	 * @return 생성된 주문 정보와 결제 후 잔액
	 */
	public synchronized OrderResponse order(long userId, long menuId) {
		CoffeeMenu menu = menuService.getMenu(menuId);
		long remainingBalance = pointService.deduct(userId, menu.price());
		CoffeeOrder order = orderRepository.save(userId, menu.id(), menu.price(), clock.instant());
		orderEventClient.send(new OrderEvent(userId, menu.id(), menu.price()));

		return new OrderResponse(order.id(), userId, menu.id(), menu.price(), remainingBalance);
	}
}
