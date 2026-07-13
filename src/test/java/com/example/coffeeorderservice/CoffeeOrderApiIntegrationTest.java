package com.example.coffeeorderservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.coffeeorderservice.common.ApiExceptionHandler;
import com.example.coffeeorderservice.menu.MenuController;
import com.example.coffeeorderservice.order.CoffeeOrderJpaRepository;
import com.example.coffeeorderservice.order.MockDataPlatformOrderEventClient;
import com.example.coffeeorderservice.order.OrderController;
import com.example.coffeeorderservice.order.OutboxEventRepository;
import com.example.coffeeorderservice.point.PointController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
class CoffeeOrderApiIntegrationTest {

	@Autowired
	private MenuController menuController;

	@Autowired
	private PointController pointController;

	@Autowired
	private OrderController orderController;

	@Autowired
	private ApiExceptionHandler apiExceptionHandler;

	@Autowired
	private CoffeeOrderJpaRepository coffeeOrderJpaRepository;

	@Autowired
	private OutboxEventRepository outboxEventRepository;

	@Autowired
	private MockDataPlatformOrderEventClient orderEventClient;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		outboxEventRepository.deleteAll();
		coffeeOrderJpaRepository.deleteAll();
		jdbcTemplate.update("delete from user_points");
		orderEventClient.clearSentEvents();
		mockMvc = MockMvcBuilders.standaloneSetup(menuController, pointController, orderController)
				.setControllerAdvice(apiExceptionHandler)
				.build();
	}

	@Test
	void 메뉴_조회부터_충전_주문_인기메뉴_조회까지_API_계약을_만족한다() throws Exception {
		mockMvc.perform(get("/api/menus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].menuId").value(1))
				.andExpect(jsonPath("$[0].price").value(4_500));

		mockMvc.perform(post("/api/points/charge")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"amount\":10000}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.balance").value(10_000));

		mockMvc.perform(post("/api/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"userId\":1,\"menuId\":1}"))
				.andExpect(status().isCreated())
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
