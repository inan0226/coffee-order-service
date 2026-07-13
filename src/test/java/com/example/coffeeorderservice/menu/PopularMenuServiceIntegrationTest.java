package com.example.coffeeorderservice.menu;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coffeeorderservice.order.CoffeeOrderEntity;
import com.example.coffeeorderservice.order.CoffeeOrderJpaRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest
@Import(PopularMenuServiceIntegrationTest.FixedClockConfiguration.class)
class PopularMenuServiceIntegrationTest {

	private static final Instant NOW = Instant.parse("2026-07-10T00:00:00Z");

	@Autowired
	private PopularMenuService popularMenuService;

	@Autowired
	private CoffeeOrderJpaRepository coffeeOrderJpaRepository;

	@BeforeEach
	void setUp() {
		coffeeOrderJpaRepository.deleteAll();
	}

	@Test
	void 최근_7일_성공_주문을_DB에서_정확히_집계해_상위_3개를_결정적으로_반환한다() {
		saveOrder(1L, NOW);
		saveOrder(1L, NOW.minusSeconds(60));
		saveOrder(1L, NOW.minusSeconds(120));
		saveOrder(2L, NOW.minusSeconds(180));
		saveOrder(2L, NOW.minusSeconds(240));
		saveOrder(3L, NOW.minusSeconds(300));
		saveOrder(3L, NOW.minusSeconds(360));
		saveOrder(4L, NOW.minusSeconds(420));
		saveOrder(4L, NOW.minusSeconds(480));
		saveOrder(4L, NOW.minusSeconds(540));
		saveOrder(4L, NOW.minusSeconds(7 * 24 * 60 * 60L + 1));
		saveOrder(1L, NOW.minusSeconds(7 * 24 * 60 * 60L));

		assertThat(popularMenuService.getPopularMenus())
				.extracting(PopularMenuResponse::menuId, PopularMenuResponse::name,
						PopularMenuResponse::price, PopularMenuResponse::orderCount)
				.containsExactly(
						org.assertj.core.groups.Tuple.tuple(1L, "Americano", 4_500L, 4L),
						org.assertj.core.groups.Tuple.tuple(4L, "Cafe Mocha", 6_000L, 3L),
						org.assertj.core.groups.Tuple.tuple(2L, "Cafe Latte", 5_000L, 2L)
				);
	}

	private void saveOrder(long menuId, Instant orderedAt) {
		long price = switch ((int) menuId) {
			case 1 -> 4_500L;
			case 2 -> 5_000L;
			case 3 -> 5_500L;
			default -> 6_000L;
		};
		coffeeOrderJpaRepository.save(new CoffeeOrderEntity(1L, menuId, price, orderedAt));
	}

	@TestConfiguration
	static class FixedClockConfiguration {

		@Bean
		@Primary
		Clock fixedClock() {
			return Clock.fixed(NOW, ZoneOffset.UTC);
		}
	}
}
