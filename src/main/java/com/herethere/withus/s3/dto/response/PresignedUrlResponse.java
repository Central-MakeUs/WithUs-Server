package com.herethere.withus.s3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "S3 Presigned URL 발급 응답")
public record PresignedUrlResponse(

	@Schema(
		description = "S3에 이미지를 업로드하기 위한 임시 URL (PUT 요청용, 60분 유효)",
		example = "https://withus-bucket.s3.ap-northeast-2.amazonaws.com/users/12/profile/abc123.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=..."
	)
	String uploadUrl,

	@Schema(
		description = "업로드된 이미지를 조회하기 위한 임시 URL (GET 요청용, 60분 유효)",
		example = "https://withus-bucket.s3.ap-northeast-2.amazonaws.com/users/12/profile/abc123.jpg"
	)
	String accessUrl,

	@Schema(
		description = "이미지 식별용 key 값 (도메인 API 요청 시 전달)",
		example = "users/12/profile/abc123.jpg"
	)
	String imageKey
) {
}
