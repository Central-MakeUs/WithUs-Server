package com.herethere.withus.keyword.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "키워드 사진 조회 응답")
public record TodayKeywordResponse(
	@Schema(description = "커플-키워드 매핑 고유 ID", example = "101")
	Long coupleKeywordId,

	@Schema(description = "오늘의 질문 내용", example = "오늘의 밥타임 사진은?")
	String question,

	@Schema(description = "나의 사진 정보")
	MemberInfo myInfo,

	@Schema(description = "상대방의 사진 정보")
	MemberInfo partnerInfo
) {
	@Builder
	@Schema(description = "사진 정보")
	public record MemberInfo(
		@Schema(description = "사용자 고유 id", example = "123")
		Long userId,

		@Schema(description = "사용자 닉네임", example = "김철수")
		String name,

		@Schema(description = "프로필 이미지 URL", example = "https://s3.com/profile/1.jpg", nullable = true)
		String profileImageUrl,

		@Schema(description = "오늘 업로드한 사진 URL (미업로드 시 null)", example = "https://s3.com/question/101.jpg", nullable = true)
		String questionImageUrl,

		@Schema(type = "string", pattern = "HH:mm", example = "14:12", description = "인증 사진 업로드 시각 (UTC 기준, 미업로드 시 null)", nullable = true)
		@JsonFormat(pattern = "HH:mm", timezone = "UTC")
		LocalDateTime answeredAt
	) {
	}
}
