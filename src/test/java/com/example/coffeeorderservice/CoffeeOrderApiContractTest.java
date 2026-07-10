package com.example.coffeeorderservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.coffeeorderservice.common.ApiExceptionHandler;
import com.example.coffeeorderservice.menu.CoffeeMenu;
import com.example.coffeeorderservice.menu.MenuController;
import com.example.coffeeorderservice.menu.MenuRepository;
import com.example.coffeeorderservice.menu.MenuService;
import com.example.coffeeorderservice.menu.PopularMenuService;
import com.example.coffeeorderservice.order.InMemoryOrderRepository;
import com.example.coffeeorderservice.order.OrderController;
import com.example.coffeeorderservice.order.OrderEventClient;
import com.example.coffeeorderservice.order.OrderService;
import com.example.coffeeorderservice.point.InMemoryPointBalanceStore;
import com.example.coffeeorderservice.point.PointController;
import com.example.coffeeorderservice.point.PointService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CoffeeOrderApiContractTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MenuRepository menuRepository = mock(MenuRepository.class);
		CoffeeMenu americano = new CoffeeMenu(1L, "Americano", 4_500L);
		CoffeeMenu cafeLatte = new CoffeeMenu(2L, "Cafe Latte", 5_000L);
		CoffeeMenu cappuccino = new CoffeeMenu(3L, "Cappuccino", 5_500L);
		CoffeeMenu cafeMocha = new CoffeeMenu(4L, "Cafe Mocha", 6_000L);
		when(menuRepository.findAll()).thenReturn(List.of(americano, cafeLatte, cappuccino, cafeMocha));
		when(menuRepository.findById(1L)).thenReturn(Optional.of(americano));
		when(menuRepository.findById(2L)).thenReturn(Optional.of(cafeLatte));
		when(menuRepository.findById(3L)).thenReturn(Optional.of(cappuccino));
		when(menuRepository.findById(4L)).thenReturn(Optional.of(cafeMocha));
		InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
		PointService pointService = new PointService(new InMemoryPointBalanceStore());
		Clock clock = Clock.fixed(Instant.parse("2026-07-10T00:00:00Z"), ZoneOffset.UTC);
		MenuService menuService = new MenuService(menuRepository);
		PopularMenuService popularMenuService = new PopularMenuService(menuRepository, orderRepository, clock);
		OrderEventClient orderEventClient = event -> {
		};
		OrderService orderService = new OrderService(
				menuService,
				pointService,
				orderRepository,
				orderEventClient,
				clock
		);

		mockMvc = MockMvcBuilders.standaloneSetup(
				new MenuController(menuService, popularMenuService),
				new PointController(pointService),
				new OrderController(orderService)
		)
				.setControllerAdvice(new ApiExceptionHandler())
				.build();
	}

	@Test
	void 메뉴_조회부터_충전_주문_인기메뉴_조회까지_API_계약을_만족한다() throws Exception {
		mockMvc.perform(get("/api/menus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].menuId").value(1))
				.andExpect(jsonPath("$[0].name").value("Americano"))
				.andExpect(jsonPath("$[0].price").value(4_500));

		mockMvc.perform(post("/api/points/charge")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"amount\":10000}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").value(1))
				.andExpect(jsonPath("$.balance").value(10_000));

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"menuId\":1}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderId").value(1))
				.andExpect(jsonPath("$.paidAmount").value(4_500))
				.andExpect(jsonPath("$.remainingBalance").value(5_500));

		mockMvc.perform(get("/api/menus/popular"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].menuId").value(1))
				.andExpect(jsonPath("$[0].orderCount").value(1));
	}

	@Test
	void 포인트가_부족한_주문은_400과_일관된_오류_응답을_반환한다() throws Exception {
		mockMvc.perform(post("/api/points/charge")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"amount\":1000}"))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"menuId\":1}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INSUFFICIENT_POINTS"));
	}

	@Test
	void 잘못된_충전_금액은_400을_반환한다() throws Exception {
		mockMvc.perform(post("/api/points/charge")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"amount\":0}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
	}
}
