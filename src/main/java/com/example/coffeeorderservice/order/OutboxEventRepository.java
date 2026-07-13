package com.example.coffeeorderservice.order;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

/**
 * 아웃박스 이벤트를 저장하고 여러 인스턴스가 안전하게 나눠 처리하도록 조회하는 Repository입니다.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

	/**
	 * 아직 전송하지 않았거나 처리 중 상태가 오래된 이벤트를 행 잠금과 함께 가져옵니다.
	 *
	 * @param pending 아직 전송하지 않은 상태
	 * @param processing 다른 인스턴스가 잡았지만 시간이 지나 다시 처리할 수 있는 상태
	 * @param expiredBefore 처리 권한이 만료된 기준 시각
	 * @param pageable 한 번에 가져올 이벤트 개수
	 * @return 다른 인스턴스와 중복 처리되지 않도록 잠긴 이벤트 목록
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			select event from OutboxEvent event
			where event.status = :pending
			   or (event.status = :processing and event.claimedAt < :expiredBefore)
			order by event.id
			""")
	List<OutboxEvent> findClaimableEvents(
			@Param("pending") OutboxStatus pending,
			@Param("processing") OutboxStatus processing,
			@Param("expiredBefore") Instant expiredBefore,
			Pageable pageable
	);
}
