package com.herethere.withus.question.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "오늘의 질문 상세 조회 응답")
public record TodayQuestionResponse(
	@Schema(description = "오늘의 질문-커플 매핑 고유 ID (사진 업로드 시 이 ID를 사용하세요)", example = "505")
	Long coupleQuestionId,

	@Schema(description = "오늘의 질문 내용", example = "상대가 가장 사랑스러워 보였던 순간은 언제인가요?")
	String question,

	@Schema(description = "나의 답변 정보")
	MemberInfo myInfo,

	@Schema(description = "상대방의 답변 정보")
	MemberInfo partnerInfo
) {
	@Builder
	@Schema(description = "답변 상세 정보")
	public record MemberInfo(
		@Schema(description = "사용자 닉네임", example = "김철수")
		String name,

		@Schema(description = "프로필 이미지 URL", example = "https://s3.com/profiles/1.jpg")
		String profileImageUrl,

		@Schema(description = "업로드한 답변 사진 URL (미업로드 시 null)", example = "https://s3.com/questions/505_ans.jpg")
		String questionImageUrl,

		@Schema(type = "string", pattern = "HH:mm", example = "20:30", description = "답변 시각 (UTC 기준, 미업로드 시 null)")
		@JsonFormat(pattern = "HH:mm", timezone = "UTC")
		LocalDateTime answeredAt
	) {
	}
}
