package com.herethere.withus.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 응답")
public record RefreshTokenResponse(

	@Schema(
		description = "새로 발급된 1시간 유효한 Access Token",
		example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmlja25hbWUiOiLqso3siqR0In0..."
	)
	String accessToken,

	@Schema(
		description = "새로 발급된 28일 유효한 Refresh Token",
		example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzM5MzM2OTI0fQ..."
	)
	String refreshToken
) {
}
