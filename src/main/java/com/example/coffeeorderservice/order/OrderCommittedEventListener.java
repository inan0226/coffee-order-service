package com.example.coffeeorderservice.order;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 주문 트랜잭션이 커밋된 뒤 즉시 아웃박스 전송을 시도하는 리스너입니다.
 */
@Component
public class OrderCommittedEventListener {

	private final OutboxDispatcher outboxDispatcher;

	public OrderCommittedEventListener(OutboxDispatcher outboxDispatcher) {
		this.outboxDispatcher = outboxDispatcher;
	}

	/**
	 * DB 커밋 후에만 전송을 시작합니다. 롤백된 주문은 아웃박스에도 저장되지 않아 전송되지 않습니다.
	 *
	 * @param event 주문 커밋 이벤트
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void dispatchAfterCommit(OrderCommittedEvent event) {
		outboxDispatcher.dispatchPendingEvents();
	}
}
