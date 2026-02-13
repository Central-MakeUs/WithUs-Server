package com.herethere.withus.archive.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record ArchiveQuestionDetailResponse(
	@Schema(description = "couple-question이 연결된 id 값입니다. 추후 상세 조회 때 사용합니다.", example = "12")
	Long coupleQuestionId,

	@Schema(description = "번호")
	Long questionNumber,

	@Schema(description = "질문 내용")
	String questionContent,

	@Schema(description = "내 사진 정보")
	ImageInfo myInfo,

	@Schema(description = "파트너 사진 정보")
	ImageInfo partnerInfo
) {
	@Schema(description = "사진 정보")
	public record ImageInfo(
		@Schema(description = "사용자 고유 id", example = "123")
		Long userId,

		@Schema(description = "사용자 닉네임", example = "김철수")
		String name,

		@Schema(description = "프로필 썸네일 이미지 URL (미업로드 시 null)", example = "https://s3.com/profile/1.jpg", nullable = true)
		String profileThumbnailImageUrl,

		@Schema(description = "오늘 업로드한 사진 URL (미업로드 시 null)", example = "https://s3.com/question/101.jpg", nullable = true)
		String answerImageUrl,

		@Schema(type = "string", pattern = "HH:mm", example = "14:12", description = "인증 사진 업로드 시각 (Asia/Seoul 기준, 미업로드 시 null)", nullable = true)
		@JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
		LocalDateTime answeredAt
	) {
	}
}
