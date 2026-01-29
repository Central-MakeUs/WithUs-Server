package com.herethere.withus.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserOnboardingResponse(
	@Schema(description = "유저 고유 ID", example = "1")
	Long userId,

	@Schema(description = "닉네임", example = "하늘이")
	String nickname,

	@Schema(
		description = "프로필 이미지 URL",
		example = "https://s3.com/profiles/1/uuid_image.jpg",
		nullable = true
	)
	String profileImageUrl
) {
}

