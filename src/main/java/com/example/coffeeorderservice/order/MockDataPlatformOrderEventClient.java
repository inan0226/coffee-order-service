package com.example.coffeeorderservice.order;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockDataPlatformOrderEventClient implements OrderEventClient {

	private static final Logger log = LoggerFactory.getLogger(MockDataPlatformOrderEventClient.class);

	private final List<OrderEvent> sentEvents = new CopyOnWriteArrayList<>();

	@Override
	public void send(OrderEvent event) {
		sentEvents.add(event);
		log.info("Mock 데이터 수집 플랫폼으로 주문 이벤트를 전송했습니다. userId={}, menuId={}, paidAmount={}",
				event.userId(), event.menuId(), event.paidAmount());
	}

	public List<OrderEvent> getSentEvents() {
		return List.copyOf(sentEvents);
	}
}
