package com.example.coffeeorderservice.point;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 포인트 충전과 차감 규칙을 담당하는 Service입니다.
 *
 * <p>공유 DB의 원자 쿼리를 사용하므로 서버 인스턴스가 여러 개여도
 * 포인트 증가와 차감이 서로 덮어써지지 않습니다.</p>
 */
@Service
public class PointService {

	private final PointBalanceStore pointBalanceStore;
	private final PointChargeTransactionService pointChargeTransactionService;

	public PointService(
			PointBalanceStore pointBalanceStore,
			PointChargeTransactionService pointChargeTransactionService
	) {
		this.pointBalanceStore = pointBalanceStore;
		this.pointChargeTransactionService = pointChargeTransactionService;
	}

	/**
	 * 요청 금액만큼 사용자의 포인트를 충전합니다.
	 *
	 * <p>처음 충전하는 사용자라면 잔액 0에서 시작합니다. 충전 금액은 반드시 양수여야 하며,
	 * long 범위를 넘는 충전도 거절합니다.</p>
	 *
	 * @param userId 충전할 사용자 식별값
	 * @param amount 충전 금액이자 추가할 포인트
	 * @return 충전 후의 사용자 ID와 포인트 잔액
	 */
	public PointBalanceResponse charge(long userId, long amount) {
		if (userId <= 0) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST);
		}
		if (amount <= 0) {
			throw new BusinessException(ErrorCode.INVALID_CHARGE_AMOUNT);
		}

		try {
			return new PointBalanceResponse(
					userId,
					pointChargeTransactionService.chargeExistingOrCreate(userId, amount)
			);
		} catch (DuplicateKeyException duplicateUserPoint) {
			return new PointBalanceResponse(
					userId,
					pointChargeTransactionService.chargeExisting(userId, amount)
			);
		} catch (DataIntegrityViolationException overflow) {
			throw new BusinessException(ErrorCode.POINT_BALANCE_OVERFLOW);
		}
	}

	/**
	 * 주문 금액만큼 사용자의 포인트를 차감합니다.
	 *
	 * <p>포인트 정보가 없으면 {@code USER_NOT_FOUND}, 잔액이 부족하면
	 * {@code INSUFFICIENT_POINTS} 예외를 발생시킵니다. 예외가 발생한 경우 잔액은 저장하지 않습니다.</p>
	 *
	 * @param userId 결제할 사용자 식별값
	 * @param amount 차감할 포인트
	 * @return 차감 후 남은 포인트 잔액
	 */
	@Transactional
	public long deduct(long userId, long amount) {
		if (amount <= 0) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST);
		}

		if (pointBalanceStore.deductIfEnough(userId, amount) == 0) {
			if (!pointBalanceStore.exists(userId)) {
				throw new BusinessException(ErrorCode.USER_NOT_FOUND);
			}
			throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS);
		}

		return currentBalance(userId);
	}

	private long currentBalance(long userId) {
		return pointBalanceStore.findBalance(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}
