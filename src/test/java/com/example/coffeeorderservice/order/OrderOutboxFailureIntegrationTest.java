package com.example.coffeeorderservice.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.coffeeorderservice.point.PointBalanceStore;
import com.example.coffeeorderservice.point.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Import(OrderOutboxFailureIntegrationTest.FailingOrderEventClientConfig.class)
class OrderOutboxFailureIntegrationTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private PointService pointService;

	@Autowired
	private PointBalanceStore pointBalanceStore;

	@Autowired
	private CoffeeOrderJpaRepository coffeeOrderJpaRepository;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		outboxEventRepository.deleteAll();
		coffeeOrderJpaRepository.deleteAll();
		jdbcTemplate.update("delete from user_points");
	}

	@Test
	void 외부_전송에_실패해도_확정된_주문의_포인트를_되돌리지_않고_아웃박스를_재시도_상태로_남긴다() {
		pointService.charge(1L, 10_000L);

		OrderResponse response = orderService.order(1L, 1L);

		assertThat(response.remainingBalance()).isEqualTo(5_500L);
		assertThat(pointBalanceStore.findBalance(1L)).contains(5_500L);
		assertThat(coffeeOrderJpaRepository.count()).isEqualTo(1L);
		assertThat(outboxEventRepository.findAll())
				.extracting(OutboxEvent::status)
				.containsExactly(OutboxStatus.PENDING);
	}

	@TestConfiguration(proxyBeanMethods = false)
	static class FailingOrderEventClientConfig {

		@Bean
		@Primary
		OrderEventClient failingOrderEventClient() {
			return event -> {
				throw new IllegalStateException("데이터 수집 플랫폼 전송 실패");
			};
		}
	}
}
