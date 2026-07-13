package com.example.coffeeorderservice.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 아웃박스에 저장된 주문 이벤트를 외부 데이터 수집 플랫폼으로 전송하는 Service입니다.
 */
@Service
public class OutboxDispatcher {

	private static final Logger log = LoggerFactory.getLogger(OutboxDispatcher.class);

	private final OutboxClaimService outboxClaimService;
	private final OrderEventClient orderEventClient;

	public OutboxDispatcher(OutboxClaimService outboxClaimService, OrderEventClient orderEventClient) {
		this.outboxClaimService = outboxClaimService;
		this.orderEventClient = orderEventClient;
	}

	/**
	 * 확보 가능한 이벤트를 전송하고 결과 상태를 DB에 기록합니다.
	 */
	public void dispatchPendingEvents() {
		for (OutboxMessage message : outboxClaimService.claimPendingEvents()) {
			try {
				orderEventClient.send(message.orderEvent());
				if (!outboxClaimService.markSent(message)) {
					log.info("다른 인스턴스가 아웃박스 이벤트 상태를 갱신했습니다. outboxEventId={}",
							message.outboxEventId());
				}
			} catch (RuntimeException exception) {
				log.warn("주문 이벤트 전송에 실패했습니다. outboxEventId={}", message.outboxEventId(), exception);
				if (!outboxClaimService.releaseForRetry(message)) {
					log.info("다른 인스턴스가 아웃박스 이벤트 상태를 갱신했습니다. outboxEventId={}",
							message.outboxEventId());
				}
			}
		}
	}

	/**
	 * 커밋 직후 전송에 실패한 이벤트를 주기적으로 다시 시도합니다.
	 */
	@Scheduled(fixedDelayString = "${outbox.retry-delay-ms:1000}")
	public void retryPendingEvents() {
		dispatchPendingEvents();
	}
}
