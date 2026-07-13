package com.example.coffeeorderservice.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 포인트 결제가 완료된 주문을 데이터베이스에 저장하는 Entity입니다.
 *
 * <p>포인트 차감과 같은 트랜잭션 안에서 저장되므로, 결제에 실패한 주문은 이 테이블에 남지 않습니다.</p>
 */
@Entity
@Table(name = "coffee_orders")
public class CoffeeOrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private long userId;

	@Column(name = "menu_id", nullable = false)
	private long menuId;

	@Column(name = "paid_amount", nullable = false)
	private long paidAmount;

	@Column(name = "ordered_at", nullable = false)
	private Instant orderedAt;

	protected CoffeeOrderEntity() {
	}

	public CoffeeOrderEntity(long userId, long menuId, long paidAmount, Instant orderedAt) {
		this.userId = userId;
		this.menuId = menuId;
		this.paidAmount = paidAmount;
		this.orderedAt = orderedAt;
	}

	public long id() {
		return id;
	}

	public long userId() {
		return userId;
	}

	public long menuId() {
		return menuId;
	}

	public long paidAmount() {
		return paidAmount;
	}

	public Instant orderedAt() {
		return orderedAt;
	}
}
