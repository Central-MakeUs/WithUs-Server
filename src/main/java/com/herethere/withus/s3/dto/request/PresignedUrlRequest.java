package com.herethere.withus.s3.dto.request;

import com.herethere.withus.s3.domain.ImageType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Presigned URL 발급 요청")
public record PresignedUrlRequest(
	@Schema(
		description = """
			업로드할 이미지의 타입 (용도):
			- **PROFILE**: 유저 개인 프로필 이미지 (저장 경로: /users/{userId}/profile/{uuid}.jpg)
			- **MEMORY**: 커플 질문/키워드 인증 사진 (저장 경로: /users/{userId}/memory/{uuid}.jpg)
			""",
		example = "MEMORY"
	)
	@NotNull(message = "이미지 타입은 필수입니다.")
	ImageType imageType
) {
}
