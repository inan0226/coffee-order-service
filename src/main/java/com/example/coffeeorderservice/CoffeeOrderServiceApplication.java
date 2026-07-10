package com.example.coffeeorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 커피 주문 서비스의 실행 시작점입니다.
 *
 * <p>애플리케이션을 실행하면 Spring Boot가 이 클래스를 기준으로
 * Controller, Service, Repository 같은 Spring 컴포넌트를 찾아 등록합니다.</p>
 */
@SpringBootApplication
public class CoffeeOrderServiceApplication {

	/**
	 * Spring Boot 서버를 시작합니다.
	 *
	 * @param args 실행 시 전달하는 옵션
	 */
	public static void main(String[] args) {
		SpringApplication.run(CoffeeOrderServiceApplication.class, args);
	}

}
