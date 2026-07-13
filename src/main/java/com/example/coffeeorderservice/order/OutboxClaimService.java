package com.example.coffeeorderservice.order;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 아웃박스 이벤트의 처리 권한을 DB 행 잠금으로 관리하는 Service입니다.
 */
@Service
public class OutboxClaimService {

	private static final int CLAIM_BATCH_SIZE = 20;
	private static final Duration CLAIM_TIMEOUT = Duration.ofMinutes(1);

	private final OutboxEventRepository outboxEventRepository;
	private final Clock clock;

	public OutboxClaimService(OutboxEventRepository outboxEventRepository, Clock clock) {
		this.outboxEventRepository = outboxEventRepository;
		this.clock = clock;
	}

	/**
	 * 여러 인스턴스 중 현재 인스턴스가 전송할 이벤트 묶음을 확보합니다.
	 *
	 * @return 처리 중 상태로 바꾼 이벤트 메시지 목록
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<OutboxMessage> claimPendingEvents() {
		Instant now = clock.instant();
		List<OutboxEvent> events = outboxEventRepository.findClaimableEvents(
				OutboxStatus.PENDING,
				OutboxStatus.PROCESSING,
				now.minus(CLAIM_TIMEOUT),
				PageRequest.of(0, CLAIM_BATCH_SIZE)
		);

		events.forEach(event -> event.claim(now));
		return events.stream()
				.map(event -> new OutboxMessage(event.id(), event.toOrderEvent()))
				.toList();
	}

	/**
	 * 외부 플랫폼 전송 성공을 기록합니다.
	 *
	 * @param outboxEventId 전송에 성공한 이벤트 ID
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void markSent(long outboxEventId) {
		OutboxEvent event = outboxEventRepository.findById(outboxEventId)
				.orElseThrow(() -> new IllegalStateException("아웃박스 이벤트를 찾을 수 없습니다."));
		event.markSent(clock.instant());
	}

	/**
	 * 외부 플랫폼 전송 실패 이벤트를 다시 전송 가능한 상태로 되돌립니다.
	 *
	 * @param outboxEventId 재시도할 이벤트 ID
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void releaseForRetry(long outboxEventId) {
		OutboxEvent event = outboxEventRepository.findById(outboxEventId)
				.orElseThrow(() -> new IllegalStateException("아웃박스 이벤트를 찾을 수 없습니다."));
		event.releaseForRetry();
	}
}
