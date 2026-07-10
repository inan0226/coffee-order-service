package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.coffeeorderservice.order.InMemoryOrderRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PopularMenuServiceTest {

	private final Instant now = Instant.parse("2026-07-10T00:00:00Z");
	private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
	private final MenuRepository menuRepository = mock(MenuRepository.class);
	private final PopularMenuService popularMenuService = new PopularMenuService(
			menuRepository,
			orderRepository,
			Clock.fixed(now, ZoneOffset.UTC)
	);

	@Test
	void 최근_7일_성공_주문을_정확히_집계해_상위_3개를_결정적으로_반환한다() {
		when(menuRepository.findById(1L)).thenReturn(Optional.of(new CoffeeMenu(1L, "Americano", 4_500L)));
		when(menuRepository.findById(2L)).thenReturn(Optional.of(new CoffeeMenu(2L, "Cafe Latte", 5_000L)));
		when(menuRepository.findById(3L)).thenReturn(Optional.of(new CoffeeMenu(3L, "Cappuccino", 5_500L)));
		when(menuRepository.findById(4L)).thenReturn(Optional.of(new CoffeeMenu(4L, "Cafe Mocha", 6_000L)));

		orderRepository.save(1L, 1L, 4_500L, now);
		orderRepository.save(2L, 1L, 4_500L, now.minusSeconds(60));
		orderRepository.save(3L, 1L, 4_500L, now.minusSeconds(120));
		orderRepository.save(1L, 2L, 5_000L, now.minusSeconds(180));
		orderRepository.save(2L, 2L, 5_000L, now.minusSeconds(240));
		orderRepository.save(1L, 3L, 5_500L, now.minusSeconds(300));
		orderRepository.save(2L, 3L, 5_500L, now.minusSeconds(360));
		orderRepository.save(1L, 4L, 6_000L, now.minusSeconds(420));
		orderRepository.save(2L, 4L, 6_000L, now.minusSeconds(480));
		orderRepository.save(3L, 4L, 6_000L, now.minusSeconds(540));
		orderRepository.save(1L, 4L, 6_000L, now.minusSeconds(7 * 24 * 60 * 60L).minusNanos(1));
		orderRepository.save(1L, 1L, 4_500L, now.minusSeconds(7 * 24 * 60 * 60L));

		assertThat(popularMenuService.getPopularMenus())
				.extracting(PopularMenuResponse::menuId, PopularMenuResponse::name,
						PopularMenuResponse::price, PopularMenuResponse::orderCount)
				.containsExactly(
						org.assertj.core.groups.Tuple.tuple(1L, "Americano", 4_500L, 4L),
						org.assertj.core.groups.Tuple.tuple(4L, "Cafe Mocha", 6_000L, 3L),
						org.assertj.core.groups.Tuple.tuple(2L, "Cafe Latte", 5_000L, 2L)
				);
	}
}
