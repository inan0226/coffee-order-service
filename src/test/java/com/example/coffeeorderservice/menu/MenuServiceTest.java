package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class MenuServiceTest {

	private final MenuService menuService = new MenuService(new InMemoryMenuRepository());

	@Test
	void 메뉴_목록은_메뉴_ID_이름_가격을_반환한다() {
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
