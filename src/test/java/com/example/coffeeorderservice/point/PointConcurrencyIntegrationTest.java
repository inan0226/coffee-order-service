package com.example.coffeeorderservice.point;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class PointConcurrencyIntegrationTest {

	@Autowired
	private PointService pointService;

	@Autowired
	private PointBalanceStore pointBalanceStore;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final ExecutorService executorService = Executors.newFixedThreadPool(20);

	@BeforeEach
	void setUp() {
		jdbcTemplate.update("delete from user_points");
	}

	@AfterEach
	void tearDown() {
		executorService.shutdownNow();
	}

	@Test
	void 여러_인스턴스_요청을_가정한_동시_충전은_모두_잔액에_반영된다() throws Exception {
		int requestCount = 20;
		CountDownLatch ready = new CountDownLatch(requestCount);
		CountDownLatch start = new CountDownLatch(1);
		List<Callable<PointBalanceResponse>> tasks = new ArrayList<>();

		for (int index = 0; index < requestCount; index++) {
			tasks.add(() -> {
				ready.countDown();
				start.await();
				return pointService.charge(1L, 100L);
			});
		}

		List<Future<PointBalanceResponse>> futures = new ArrayList<>();
		for (Callable<PointBalanceResponse> task : tasks) {
			futures.add(executorService.submit(task));
		}

		ready.await();
		start.countDown();
		for (Future<PointBalanceResponse> future : futures) {
			future.get();
		}

		assertThat(pointBalanceStore.findBalance(1L)).contains(2_000L);
	}
}
