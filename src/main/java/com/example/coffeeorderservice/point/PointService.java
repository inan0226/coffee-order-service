package com.example.coffeeorderservice.point;

import com.example.coffeeorderservice.common.BusinessException;
import com.example.coffeeorderservice.common.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class PointService {

	private final PointRepository pointRepository;

	public PointService(PointRepository pointRepository) {
		this.pointRepository = pointRepository;
	}

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
}
