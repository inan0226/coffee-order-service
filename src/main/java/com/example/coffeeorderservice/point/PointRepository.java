package com.example.coffeeorderservice.point;

import java.util.Optional;

public interface PointRepository {

	Optional<Long> findBalanceByUserId(long userId);

	void save(long userId, long balance);
}
