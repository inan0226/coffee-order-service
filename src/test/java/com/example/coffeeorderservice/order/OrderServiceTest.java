package com.example.coffeeorderservice.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import com.example.coffeeorderservice.menu.InMemoryMenuRepository;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.point.InMemoryPointRepository;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

	private final InMemoryPointRepository pointRepository = new InMemoryPointRepository();
	private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
	private final RecordingOrderEventClient orderEventClient = new RecordingOrderEventClient();
	private final PointService pointService = new PointService(pointRepository);
	private final OrderService orderService = new OrderService(
			new MenuService(new InMemoryMenuRepository()),
			pointService,
			orderRepository,
			orderEventClient,
			Clock.fixed(Instant.parse("2026-07-10T00:00:00Z"), ZoneOffset.UTC)
	);

	@BeforeEach
	void setUp() {
		pointService.charge(1L, 5_000L);
	}

	@Test
	void 주문에_성공하면_포인트를_차감하고_이벤트를_한번_전송한다() {
		OrderResponse response = orderService.order(1L, 1L);

		assertThat(response)
				.isEqualTo(new OrderResponse(1L, 1L, 1L, 4_500L, 500L));
		assertThat(orderEventClient.events)
				.containsExactly(new OrderEvent(1L, 1L, 4_500L));
	}

	@Test
	void 포인트가_부족하면_포인트를_차감하지_않고_이벤트도_전송하지_않는다() {
		assertThatThrownBy(() -> orderService.order(1L, 4L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINTS));

		assertThat(pointRepository.findBalanceByUserId(1L)).contains(5_000L);
		assertThat(orderRepository.findAll()).isEmpty();
		assertThat(orderEventClient.events).isEmpty();
	}

	@Test
	void 존재하지_않는_메뉴를_주문하면_포인트를_차감하지_않고_이벤트도_전송하지_않는다() {
		assertThatThrownBy(() -> orderService.order(1L, 999L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.MENU_NOT_FOUND));

		assertThat(pointRepository.findBalanceByUserId(1L)).contains(5_000L);
		assertThat(orderRepository.findAll()).isEmpty();
		assertThat(orderEventClient.events).isEmpty();
	}

	private static class RecordingOrderEventClient implements OrderEventClient {

		private final List<OrderEvent> events = new ArrayList<>();

		@Override
		public void send(OrderEvent event) {
			events.add(event);
		}
	}
}
