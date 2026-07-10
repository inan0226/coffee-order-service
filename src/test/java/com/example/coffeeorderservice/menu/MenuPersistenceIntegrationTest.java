package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MenuPersistenceIntegrationTest {

	@Autowired
	private MenuRepository menuRepository;

	@Test
	void Flyway로_초기화한_메뉴를_공유_데이터베이스에서_조회한다() {
		List<CoffeeMenu> menus = menuRepository.findAll();

		assertThat(menus)
				.extracting(CoffeeMenu::id, CoffeeMenu::name, CoffeeMenu::price)
				.containsExactlyInAnyOrder(
						org.assertj.core.groups.Tuple.tuple(1L, "Americano", 4_500L),
						org.assertj.core.groups.Tuple.tuple(2L, "Cafe Latte", 5_000L),
						org.assertj.core.groups.Tuple.tuple(3L, "Cappuccino", 5_500L),
						org.assertj.core.groups.Tuple.tuple(4L, "Cafe Mocha", 6_000L)
				);
	}
}
