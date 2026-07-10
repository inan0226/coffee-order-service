package com.example.coffeeorderservice.order;

public record OrderEvent(long userId, long menuId, long paidAmount) {
}
