package com.example.coffeeorderservice.point;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.dao.DuplicateKeyException;

/**
 * 빠른 단위 테스트에만 사용하는 메모리 포인트 저장소입니다.
 */
public class InMemoryPointBalanceStore implements PointBalanceStore {

	private final ConcurrentHashMap<Long, Long> balances = new ConcurrentHashMap<>();

	@Override
	public int increaseExisting(long userId, long amount) {
		AtomicInteger updated = new AtomicInteger();
		balances.computeIfPresent(userId, (id, balance) -> {
			updated.set(1);
			return Math.addExact(balance, amount);
		});
		return updated.get();
	}

	@Override
	public void create(long userId, long balance) {
		if (balances.putIfAbsent(userId, balance) != null) {
			throw new DuplicateKeyException("이미 존재하는 사용자 포인트입니다.");
		}
	}

	@Override
	public int deductIfEnough(long userId, long amount) {
		AtomicInteger updated = new AtomicInteger();
		balances.computeIfPresent(userId, (id, balance) -> {
			if (balance < amount) {
				return balance;
			}
			updated.set(1);
			return balance - amount;
		});
		return updated.get();
	}

	@Override
	public Optional<Long> findBalance(long userId) {
		return Optional.ofNullable(balances.get(userId));
	}

	@Override
	public boolean exists(long userId) {
		return balances.containsKey(userId);
	}
}
