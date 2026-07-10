package com.example.coffeeorderservice.order;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 실제 외부 플랫폼 대신 주문 이벤트를 받아 기록하는 Mock 클라이언트입니다.
 *
 * <p>전송된 이벤트를 메모리에 보관하고 로그에도 남기므로, 주문 성공 후 이벤트 전송이
 * 즉시 호출되는지 확인할 수 있습니다.</p>
 */
@Component
public class MockDataPlatformOrderEventClient implements OrderEventClient {

	private static final Logger log = LoggerFactory.getLogger(MockDataPlatformOrderEventClient.class);

	private final List<OrderEvent> sentEvents = new CopyOnWriteArrayList<>();

	/**
	 * 전달받은 주문 이벤트를 Mock 플랫폼 전송 기록에 추가합니다.
	 *
	 * @param event 전송할 주문 이벤트
	 */
	@Override
	public void send(OrderEvent event) {
		sentEvents.add(event);
		log.info("Mock 데이터 수집 플랫폼으로 주문 이벤트를 전송했습니다. userId={}, menuId={}, paidAmount={}",
				event.userId(), event.menuId(), event.paidAmount());
	}

	/**
	 * 지금까지 Mock 플랫폼으로 보낸 이벤트를 읽기 전용 목록으로 반환합니다.
	 *
	 * @return 전송 기록의 복사본
	 */
	public List<OrderEvent> getSentEvents() {
		return List.copyOf(sentEvents);
	}
}
