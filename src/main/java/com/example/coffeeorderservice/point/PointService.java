package com.example.coffeeorderservice.point;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import org.springframework.stereotype.Service;

/**
 * 포인트 충전과 차감 규칙을 담당하는 Service입니다.
 *
 * <p>잔액을 바꾸는 메서드는 동시에 실행되지 않도록 동기화하여,
 * 같은 사용자의 포인트가 잘못 계산될 가능성을 줄입니다.</p>
 */
@Service
public class PointService {

	private final PointRepository pointRepository;

	public PointService(PointRepository pointRepository) {
		this.pointRepository = pointRepository;
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
	public synchronized PointBalanceResponse charge(long userId, long amount) {
		if (userId <= 0) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST);
		}
		if (amount <= 0) {
			throw new BusinessException(ErrorCode.INVALID_CHARGE_AMOUNT);
		}

		long currentBalance = pointRepository.findBalanceByUserId(userId).orElse(0L);
		if (Long.MAX_VALUE - currentBalance < amount) {
			throw new BusinessException(ErrorCode.INVALID_CHARGE_AMOUNT);
		}

		long balance = currentBalance + amount;
		pointRepository.save(userId, balance);
		return new PointBalanceResponse(userId, balance);
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
	public synchronized long deduct(long userId, long amount) {
		long currentBalance = pointRepository.findBalanceByUserId(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (currentBalance < amount) {
			throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS);
		}

		long balance = currentBalance - amount;
		pointRepository.save(userId, balance);
		return balance;
	}

	/**
	 * 이미 차감한 포인트를 원래 사용자 잔액으로 되돌립니다.
	 *
	 * <p>주문 이벤트 전송처럼 결제 이후 단계가 실패했을 때만 호출합니다.
	 * 정상적인 충전 API 처리에는 사용하지 않습니다.</p>
	 *
	 * @param userId 포인트를 되돌릴 사용자 식별값
	 * @param amount 되돌릴 포인트
	 * @return 환불 후 포인트 잔액
	 */
	public synchronized long refund(long userId, long amount) {
		long currentBalance = pointRepository.findBalanceByUserId(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (Long.MAX_VALUE - currentBalance < amount) {
			throw new IllegalStateException("포인트 환불 금액이 잔액 범위를 초과했습니다.");
		}

		long balance = currentBalance + amount;
		pointRepository.save(userId, balance);
		return balance;
	}
}
