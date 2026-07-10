package com.example.coffeeorderservice.menu;

import com.example.coffeeorderservice.order.CoffeeOrder;
import com.example.coffeeorderservice.order.OrderRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 최근 7일 주문 기록을 메뉴별로 집계해 인기 메뉴를 계산하는 Service입니다.
 *
 * <p>주문 횟수가 많은 순서로 정렬하고, 횟수가 같으면 메뉴 ID가 작은 메뉴를 먼저 반환해
 * 언제 조회해도 같은 순서가 나오도록 합니다.</p>
 */
@Service
public class PopularMenuService {

	private final MenuRepository menuRepository;
	private final OrderRepository orderRepository;
	private final Clock clock;

	public PopularMenuService(MenuRepository menuRepository, OrderRepository orderRepository, Clock clock) {
		this.menuRepository = menuRepository;
		this.orderRepository = orderRepository;
		this.clock = clock;
	}

	/**
	 * 현재 시점부터 7일 전까지의 성공 주문을 집계해 상위 3개 메뉴를 반환합니다.
	 *
	 * @return 주문 횟수 내림차순, 메뉴 ID 오름차순으로 정렬된 인기 메뉴 최대 3개
	 */
	public List<PopularMenuResponse> getPopularMenus() {
		Instant now = clock.instant();
		Instant sevenDaysAgo = now.minus(7, ChronoUnit.DAYS);

		Map<Long, Long> orderCountByMenuId = orderRepository.findByOrderedAtBetween(sevenDaysAgo, now).stream()
				.collect(Collectors.groupingBy(CoffeeOrder::menuId, Collectors.counting()));

		return orderCountByMenuId.entrySet().stream()
				.sorted(Comparator.<Map.Entry<Long, Long>>comparingLong(Map.Entry::getValue)
						.reversed()
						.thenComparingLong(Map.Entry::getKey))
				.limit(3)
				.map(entry -> toResponse(entry.getKey(), entry.getValue()))
				.toList();
	}

	/**
	 * 집계 결과의 메뉴 ID를 실제 메뉴 정보와 결합합니다.
	 *
	 * @param menuId 메뉴 식별값
	 * @param orderCount 집계된 주문 횟수
	 * @return 인기 메뉴 API 응답
	 */
	private PopularMenuResponse toResponse(long menuId, long orderCount) {
		CoffeeMenu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalStateException("주문 메뉴를 찾을 수 없습니다."));
		return PopularMenuResponse.of(menu, orderCount);
	}
}
