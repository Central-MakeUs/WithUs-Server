package com.herethere.withus;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
public class WithusApplication {

	public static void main(String[] args) {
		SpringApplication.run(WithusApplication.class, args);
	}

	@PostConstruct
	public void started() {
		// 애플리케이션의 기본 시간대를 UTC로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

}
