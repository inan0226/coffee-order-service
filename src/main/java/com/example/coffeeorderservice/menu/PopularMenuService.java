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

	private PopularMenuResponse toResponse(long menuId, long orderCount) {
		CoffeeMenu menu = menuRepository.findById(menuId)
				.orElseThrow(() -> new IllegalStateException("주문 메뉴를 찾을 수 없습니다."));
		return PopularMenuResponse.of(menu, orderCount);
	}
}
