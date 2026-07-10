package com.example.coffeeorderservice.point;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPointRepository implements PointRepository {

	private final Map<Long, Long> balances = new ConcurrentHashMap<>();

	@Override
	public Optional<Long> findBalanceByUserId(long userId) {
		return Optional.ofNullable(balances.get(userId));
	}

	@Override
	public void save(long userId, long balance) {
		balances.put(userId, balance);
	}
}
