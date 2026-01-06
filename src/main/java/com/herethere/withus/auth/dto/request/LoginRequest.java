package com.herethere.withus.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank String oauthToken,
	String fcmToken) { //TODO : valid 관련 어노테이션 추가하기
}
