package com.example.coffeeorderservice.point;

import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 공유 관계형 데이터베이스에서 포인트를 원자적으로 갱신하는 JDBC 구현체입니다.
 *
 * <p>증가와 차감에 읽기-수정-저장 과정을 사용하지 않고 SQL UPDATE 한 번을 사용합니다.
 * 따라서 각 애플리케이션 인스턴스의 메모리나 JVM 잠금에 의존하지 않습니다.</p>
 */
@Repository
public class JdbcPointBalanceStore implements PointBalanceStore {

	private final JdbcTemplate jdbcTemplate;

	public JdbcPointBalanceStore(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int increaseExisting(long userId, long amount) {
		return jdbcTemplate.update(
				"update user_points set balance = balance + ? where user_id = ?",
				amount,
				userId
		);
	}

	@Override
	public void create(long userId, long balance) {
		jdbcTemplate.update(
				"insert into user_points (user_id, balance) values (?, ?)",
				userId,
				balance
		);
	}

	@Override
	public int deductIfEnough(long userId, long amount) {
		return jdbcTemplate.update(
				"update user_points set balance = balance - ? where user_id = ? and balance >= ?",
				amount,
				userId,
				amount
		);
	}

	@Override
	public Optional<Long> findBalance(long userId) {
		List<Long> balances = jdbcTemplate.query(
				"select balance from user_points where user_id = ?",
				(resultSet, rowNum) -> resultSet.getLong("balance"),
				userId
		);
		return balances.stream().findFirst();
	}

	@Override
	public boolean exists(long userId) {
		Integer count = jdbcTemplate.queryForObject(
				"select count(*) from user_points where user_id = ?",
				Integer.class,
				userId
		);
		return count != null && count > 0;
	}
}
