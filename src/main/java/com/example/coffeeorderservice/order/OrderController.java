package com.example.coffeeorderservice.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 커피 주문과 포인트 결제 HTTP 요청을 받는 Controller입니다.
 *
 * <p>{@code POST /api/orders} 요청이 성공하면 새 주문이 만들어졌다는 의미로 HTTP 201을 반환합니다.</p>
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * 메뉴 주문과 포인트 결제를 처리합니다.
	 *
	 * @param request 사용자 ID와 메뉴 ID가 담긴 요청
	 * @return 주문 정보와 결제 후 잔액이 담긴 201 응답
	 */
	@PostMapping
	public ResponseEntity<OrderResponse> order(@Valid @RequestBody OrderRequest request) {
		OrderResponse response = orderService.order(request.userId(), request.menuId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
