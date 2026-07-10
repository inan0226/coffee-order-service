package com.example.coffeeorderservice.order;

/**
 * 성공한 주문 정보를 외부 데이터 수집 플랫폼으로 보내는 통로입니다.
 *
 * <p>Service는 이 인터페이스에만 의존하므로, 과제에서는 Mock 구현체를 사용하고
 * 실제 서비스에서는 HTTP 기반 구현체로 쉽게 교체할 수 있습니다.</p>
 */
public interface OrderEventClient {

	/**
	 * 주문 이벤트를 즉시 전송합니다.
	 *
	 * @param event 전송할 사용자 ID, 메뉴 ID, 결제 금액 정보
	 */
	void send(OrderEvent event);
}
