package com.example.coffeeorderservice.point;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/points")
public class PointController {

	private final PointService pointService;

	public PointController(PointService pointService) {
		this.pointService = pointService;
	}

	@PostMapping("/charge")
	public PointBalanceResponse charge(@Valid @RequestBody PointChargeRequest request) {
		return pointService.charge(request.userId(), request.amount());
	}
}
