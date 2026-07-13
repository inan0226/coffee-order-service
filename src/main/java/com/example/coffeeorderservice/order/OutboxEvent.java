package com.example.coffeeorderservice.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 주문 데이터 수집 이벤트를 안전하게 재전송하기 위한 트랜잭션 아웃박스 Entity입니다.
 *
 * <p>주문과 같은 트랜잭션에서 저장됩니다. 서버가 주문 커밋 직후 종료돼도 이벤트가 DB에 남아
 * 다른 인스턴스의 재시도 작업이 전송을 이어갈 수 있습니다.</p>
 */
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private long userId;

	@Column(name = "menu_id", nullable = false)
	private long menuId;

	@Column(name = "paid_amount", nullable = false)
	private long paidAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OutboxStatus status;

	@Column(name = "attempt_count", nullable = false)
	private int attemptCount;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "claimed_at")
	private Instant claimedAt;

	@Column(name = "published_at")
	private Instant publishedAt;

	protected OutboxEvent() {
	}

	public OutboxEvent(long userId, long menuId, long paidAmount, Instant createdAt) {
		this.userId = userId;
		this.menuId = menuId;
		this.paidAmount = paidAmount;
		this.status = OutboxStatus.PENDING;
		this.attemptCount = 0;
		this.createdAt = createdAt;
	}

	public void claim(Instant claimedAt) {
		this.status = OutboxStatus.PROCESSING;
		this.claimedAt = claimedAt;
		this.attemptCount++;
	}

	public void markSent(Instant publishedAt) {
		this.status = OutboxStatus.SENT;
		this.claimedAt = null;
		this.publishedAt = publishedAt;
	}

	public void releaseForRetry() {
		this.status = OutboxStatus.PENDING;
		this.claimedAt = null;
	}

	public long id() {
		return id;
	}

	public OrderEvent toOrderEvent() {
		return new OrderEvent(userId, menuId, paidAmount);
	}

	public OutboxStatus status() {
		return status;
	}
}
