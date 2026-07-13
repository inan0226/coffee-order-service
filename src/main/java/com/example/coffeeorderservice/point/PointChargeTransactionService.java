package com.example.coffeeorderservice.point;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트 충전에 필요한 데이터베이스 트랜잭션을 분리해 처리하는 서비스입니다.
 *
 * <p>여러 서버가 처음 충전하는 같은 사용자의 포인트 행을 동시에 만들 수 있습니다.
 * 이때 한 요청의 INSERT가 중복 키 오류로 실패하면 해당 데이터베이스 트랜잭션은 더 이상
 * 사용할 수 없으므로, 호출자가 새 트랜잭션에서 기존 행 증가를 재시도할 수 있게 합니다.</p>
 */
@Service
public class PointChargeTransactionService {

	private final PointBalanceStore pointBalanceStore;

	public PointChargeTransactionService(PointBalanceStore pointBalanceStore) {
		this.pointBalanceStore = pointBalanceStore;
	}

	/**
	 * 기존 포인트 행의 잔액을 증가시키거나, 행이 없으면 새로 생성합니다.
	 *
	 * <p>INSERT 과정에서 중복 키 예외가 발생하면 이 트랜잭션은 롤백됩니다.
	 * 예외를 받은 호출자는 {@link #chargeExisting(long, long)}을 새 트랜잭션으로 호출합니다.</p>
	 *
	 * @param userId 충전할 사용자 식별값
	 * @param amount 추가할 포인트
	 * @return 충전 후 포인트 잔액
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public long chargeExistingOrCreate(long userId, long amount) {
		if (pointBalanceStore.increaseExisting(userId, amount) > 0) {
			return currentBalance(userId);
		}

		pointBalanceStore.create(userId, amount);
		return amount;
	}

	/**
	 * 이미 존재하는 포인트 행의 잔액을 증가시킵니다.
	 *
	 * <p>중복 생성 경쟁에서 다른 요청이 행을 먼저 만든 뒤에만 호출됩니다.</p>
	 *
	 * @param userId 충전할 사용자 식별값
	 * @param amount 추가할 포인트
	 * @return 충전 후 포인트 잔액
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public long chargeExisting(long userId, long amount) {
		if (pointBalanceStore.increaseExisting(userId, amount) == 0) {
			throw new IllegalStateException("포인트 행이 생성된 뒤 다시 조회되지 않았습니다.");
		}
		return currentBalance(userId);
	}

	private long currentBalance(long userId) {
		return pointBalanceStore.findBalance(userId)
				.orElseThrow(() -> new IllegalStateException("포인트 잔액을 조회할 수 없습니다."));
	}
}
