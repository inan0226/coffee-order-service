package com.example.coffeeorderservice.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import com.example.coffeeorderservice.menu.CoffeeMenu;
import com.example.coffeeorderservice.menu.MenuRepository;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.point.InMemoryPointBalanceStore;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServiceTest {

	private final InMemoryPointBalanceStore pointBalanceStore = new InMemoryPointBalanceStore();
	private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
	private final RecordingOrderEventClient orderEventClient = new RecordingOrderEventClient();
	private final PointService pointService = new PointService(pointBalanceStore);
	private final OrderService orderService = new OrderService(
			menuService(),
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

		assertThat(pointBalanceStore.findBalance(1L)).contains(5_000L);
		assertThat(orderRepository.findAll()).isEmpty();
		assertThat(orderEventClient.events).isEmpty();
	}

	@Test
	void 존재하지_않는_메뉴를_주문하면_포인트를_차감하지_않고_이벤트도_전송하지_않는다() {
		assertThatThrownBy(() -> orderService.order(1L, 999L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.MENU_NOT_FOUND));

		assertThat(pointBalanceStore.findBalance(1L)).contains(5_000L);
		assertThat(orderRepository.findAll()).isEmpty();
		assertThat(orderEventClient.events).isEmpty();
	}

	@Test
	void 포인트_정보가_없는_사용자는_주문할_수_없다() {
		assertThatThrownBy(() -> orderService.order(2L, 1L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND));

		assertThat(orderRepository.findAll()).isEmpty();
		assertThat(orderEventClient.events).isEmpty();
	}

	@Test
	void 주문_이벤트_전송에_실패하면_포인트와_주문_기록을_되돌린다() {
		OrderService failingOrderService = new OrderService(
				menuService(),
				pointService,
				orderRepository,
				event -> {
					throw new IllegalStateException("데이터 수집 플랫폼 연결 실패");
				},
				Clock.fixed(Instant.parse("2026-07-10T00:00:00Z"), ZoneOffset.UTC)
		);

		assertThatThrownBy(() -> failingOrderService.order(1L, 1L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode())
								.isEqualTo(ErrorCode.ORDER_EVENT_DELIVERY_FAILED));

		assertThat(pointBalanceStore.findBalance(1L)).contains(5_000L);
		assertThat(orderRepository.findAll()).isEmpty();
	}

	private static MenuService menuService() {
		MenuRepository menuRepository = mock(MenuRepository.class);
		when(menuRepository.findById(1L)).thenReturn(Optional.of(new CoffeeMenu(1L, "Americano", 4_500L)));
		when(menuRepository.findById(4L)).thenReturn(Optional.of(new CoffeeMenu(4L, "Cafe Mocha", 6_000L)));
		return new MenuService(menuRepository);
	}

	private static class RecordingOrderEventClient implements OrderEventClient {

		private final List<OrderEvent> events = new ArrayList<>();

		@Override
		public void send(OrderEvent event) {
			events.add(event);
		}
	}
}
