package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

class MenuServiceTest {

	private final MenuRepository menuRepository = mock(MenuRepository.class);
	private final MenuService menuService = new MenuService(menuRepository);

	@Test
	void 메뉴_목록은_메뉴_ID_이름_가격을_반환한다() {
		when(menuRepository.findAll()).thenReturn(List.of(
				new CoffeeMenu(1L, "Americano", 4_500L),
				new CoffeeMenu(2L, "Cafe Latte", 5_000L),
				new CoffeeMenu(3L, "Cappuccino", 5_500L),
				new CoffeeMenu(4L, "Cafe Mocha", 6_000L)
		));

		List<MenuResponse> menus = menuService.getMenus();

		assertThat(menus)
				.extracting(MenuResponse::menuId, MenuResponse::name, MenuResponse::price)
				.containsExactly(
						org.assertj.core.groups.Tuple.tuple(1L, "Americano", 4_500L),
						org.assertj.core.groups.Tuple.tuple(2L, "Cafe Latte", 5_000L),
						org.assertj.core.groups.Tuple.tuple(3L, "Cappuccino", 5_500L),
						org.assertj.core.groups.Tuple.tuple(4L, "Cafe Mocha", 6_000L)
				);
	}
}
