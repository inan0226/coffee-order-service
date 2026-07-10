package com.example.coffeeorderservice.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> order(@Valid @RequestBody OrderRequest request) {
		OrderResponse response = orderService.order(request.userId(), request.menuId());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
