package com.example.coffeeorderservice.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import com.example.coffeeorderservice.point.PointBalanceStore;
import com.example.coffeeorderservice.point.PointService;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
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
class OrderConsistencyIntegrationTest {

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
	private OutboxClaimService outboxClaimService;

	@Autowired
	private MockDataPlatformOrderEventClient orderEventClient;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final ExecutorService executorService = Executors.newFixedThreadPool(2);

	@BeforeEach
	void setUp() {
		outboxEventRepository.deleteAll();
		coffeeOrderJpaRepository.deleteAll();
		jdbcTemplate.update("delete from user_points");
		orderEventClient.clearSentEvents();
	}

	@AfterEach
	void tearDown() {
		executorService.shutdownNow();
	}

	@Test
	void 주문_성공_시_포인트_주문_아웃박스가_함께_저장되고_이벤트를_전송한다() {
		pointService.charge(1L, 10_000L);

		OrderResponse response = orderService.order(1L, 1L);

		assertThat(response.paidAmount()).isEqualTo(4_500L);
		assertThat(response.remainingBalance()).isEqualTo(5_500L);
		assertThat(pointBalanceStore.findBalance(1L)).contains(5_500L);
		assertThat(coffeeOrderJpaRepository.count()).isEqualTo(1L);
		assertThat(outboxEventRepository.findAll())
				.extracting(OutboxEvent::status)
				.containsExactly(OutboxStatus.SENT);
		assertThat(orderEventClient.getSentEvents())
				.containsExactly(new OrderEvent(1L, 1L, 4_500L));
	}

	@Test
	void 포인트가_부족하면_주문과_아웃박스_기록이_남지_않는다() {
		pointService.charge(1L, 1_000L);

		assertThatThrownBy(() -> orderService.order(1L, 1L))
				.isInstanceOfSatisfying(BusinessException.class,
						exception -> assertThat(exception.errorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINTS));

		assertThat(pointBalanceStore.findBalance(1L)).contains(1_000L);
		assertThat(coffeeOrderJpaRepository.count()).isZero();
		assertThat(outboxEventRepository.count()).isZero();
		assertThat(orderEventClient.getSentEvents()).isEmpty();
	}

	@Test
	void 동시에_같은_포인트로_주문하면_정확히_한_건만_성공한다() throws Exception {
		pointService.charge(1L, 4_500L);
		CountDownLatch ready = new CountDownLatch(2);
		CountDownLatch start = new CountDownLatch(1);

		Future<OrderResponse> firstOrder = executorService.submit(() -> orderAfterStart(ready, start));
		Future<OrderResponse> secondOrder = executorService.submit(() -> orderAfterStart(ready, start));

		ready.await();
		start.countDown();

		int successCount = 0;
		for (Future<OrderResponse> future : java.util.List.of(firstOrder, secondOrder)) {
			try {
				future.get();
				successCount++;
			} catch (ExecutionException exception) {
				assertThat(exception.getCause())
						.isInstanceOfSatisfying(BusinessException.class,
								businessException -> assertThat(businessException.errorCode())
										.isEqualTo(ErrorCode.INSUFFICIENT_POINTS));
			}
		}

		assertThat(successCount).isEqualTo(1);
		assertThat(pointBalanceStore.findBalance(1L)).contains(0L);
		assertThat(coffeeOrderJpaRepository.count()).isEqualTo(1L);
		assertThat(outboxEventRepository.count()).isEqualTo(1L);
	}

	@Test
	void 여러_인스턴스가_같은_아웃박스_이벤트를_중복으로_확보하지_않는다() throws Exception {
		outboxEventRepository.save(new OutboxEvent(1L, 1L, 4_500L, Instant.now()));
		CountDownLatch ready = new CountDownLatch(2);
		CountDownLatch start = new CountDownLatch(1);

		Future<List<OutboxMessage>> firstClaim = executorService.submit(() -> claimAfterStart(ready, start));
		Future<List<OutboxMessage>> secondClaim = executorService.submit(() -> claimAfterStart(ready, start));

		ready.await();
		start.countDown();

		int claimedCount = firstClaim.get().size() + secondClaim.get().size();

		assertThat(claimedCount).isEqualTo(1);
		assertThat(outboxEventRepository.findAll())
				.extracting(OutboxEvent::status)
				.containsExactly(OutboxStatus.PROCESSING);
	}

	@Test
	void 만료된_이전_클레임은_새_처리자의_전송_완료_상태를_되돌릴_수_없다() {
		OutboxEvent event = outboxEventRepository.save(new OutboxEvent(1L, 1L, 4_500L, Instant.now()));
		OutboxMessage expiredMessage = outboxClaimService.claimPendingEvents().getFirst();

		jdbcTemplate.update(
				"update outbox_events set claimed_at = ? where id = ?",
				java.sql.Timestamp.from(Instant.now().minusSeconds(61)),
				event.id()
		);
		OutboxMessage recoveredMessage = outboxClaimService.claimPendingEvents().getFirst();

		assertThat(outboxClaimService.markSent(recoveredMessage)).isTrue();
		assertThat(outboxClaimService.releaseForRetry(expiredMessage)).isFalse();
		assertThat(outboxEventRepository.findAll())
				.extracting(OutboxEvent::status)
				.containsExactly(OutboxStatus.SENT);
	}

	private OrderResponse orderAfterStart(CountDownLatch ready, CountDownLatch start) throws Exception {
		ready.countDown();
		start.await();
		return orderService.order(1L, 1L);
	}

	private List<OutboxMessage> claimAfterStart(CountDownLatch ready, CountDownLatch start) throws Exception {
		ready.countDown();
		start.await();
		return outboxClaimService.claimPendingEvents();
	}
}
