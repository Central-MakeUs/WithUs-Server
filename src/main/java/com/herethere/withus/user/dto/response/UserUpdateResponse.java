package com.herethere.withus.user.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 프로필 수정 응답")
public record UserUpdateResponse(
	@Schema(description = "유저 고유 ID", example = "1")
	Long userId,

	@Schema(description = "수정된 닉네임", example = "하늘이")
	String nickname,

	@Schema(description = "수정된 생일", example = "2000-01-02")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate birthday,

	@Schema(
		description = "프로필 이미지 URL",
		example = "https://s3.com/profiles/1/uuid_image.jpg",
		nullable = true
	)
	String profileImageUrl
) {
}
