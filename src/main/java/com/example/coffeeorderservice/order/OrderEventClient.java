package com.example.coffeeorderservice.order;

public interface OrderEventClient {

	void send(OrderEvent event);
}
