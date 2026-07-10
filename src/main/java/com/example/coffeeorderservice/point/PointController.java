package com.example.coffeeorderservice.point;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 포인트 충전 HTTP 요청을 받는 Controller입니다.
 *
 * <p>{@code POST /api/points/charge} 요청의 JSON을 검증한 뒤 PointService에 전달합니다.</p>
 */
@RestController
@RequestMapping("/api/points")
public class PointController {

	private final PointService pointService;

	public PointController(PointService pointService) {
		this.pointService = pointService;
	}

	/**
	 * 사용자 포인트를 충전하고 충전 후 잔액을 반환합니다.
	 *
	 * @param request 사용자 ID와 충전 금액이 담긴 요청
	 * @return 충전 후 잔액
	 */
	@PostMapping("/charge")
	public PointBalanceResponse charge(@Valid @RequestBody PointChargeRequest request) {
		return pointService.charge(request.userId(), request.amount());
	}
}
