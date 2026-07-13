package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.coffeeorderservice.order.CoffeeOrderJpaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class PopularMenuServiceTest {

	private static final Instant NOW = Instant.parse("2026-07-10T00:00:00Z");

	private final MenuRepository menuRepository = org.mockito.Mockito.mock(MenuRepository.class);
	private final CoffeeOrderJpaRepository coffeeOrderJpaRepository = org.mockito.Mockito.mock(CoffeeOrderJpaRepository.class);
	private final PopularMenuService popularMenuService = new PopularMenuService(
			menuRepository,
			coffeeOrderJpaRepository,
			Clock.fixed(NOW, ZoneOffset.UTC)
	);

	@Test
	void 인기_메뉴는_메뉴를_한번에_조회하고_집계_정렬을_유지한다() {
		List<MenuOrderCount> counts = List.of(new MenuOrderCount(2L, 3L), new MenuOrderCount(1L, 2L));
		when(coffeeOrderJpaRepository.findPopularMenuCounts(eq(NOW.minusSeconds(7 * 24 * 60 * 60L)),
				eq(NOW), any(Pageable.class))).thenReturn(counts);
		when(menuRepository.findAllById(List.of(2L, 1L))).thenReturn(List.of(
				new CoffeeMenu(1L, "Americano", 4_500L),
				new CoffeeMenu(2L, "Cafe Latte", 5_000L)
		));

		assertThat(popularMenuService.getPopularMenus())
				.extracting(PopularMenuResponse::menuId, PopularMenuResponse::orderCount)
				.containsExactly(
						org.assertj.core.groups.Tuple.tuple(2L, 3L),
						org.assertj.core.groups.Tuple.tuple(1L, 2L)
				);
		verify(menuRepository).findAllById(List.of(2L, 1L));
		verify(menuRepository, never()).findById(any(Long.class));
	}
}
