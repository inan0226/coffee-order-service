package com.example.coffeeorderservice.point;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointChargeRequest(
		@NotNull @Positive Long userId,
		@NotNull @Positive Long amount
) {
}
