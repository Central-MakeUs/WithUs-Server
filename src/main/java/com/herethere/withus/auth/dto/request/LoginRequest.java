package com.herethere.withus.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = "OAuth Token이 존재하지 않습니다.") String oauthToken,
	@NotBlank(message = "FCMToken이 존재하지 않습니다.") String fcmToken) {
}
