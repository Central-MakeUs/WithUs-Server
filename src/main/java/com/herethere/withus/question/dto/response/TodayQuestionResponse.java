package com.herethere.withus.question.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

public record TodayQuestionResponse(Long coupleQuestionId, String question, MemberInfo myInfo, MemberInfo partnerInfo) {
	@Builder
	public record MemberInfo(
		String name,
		String profileImageUrl,
		String questionImageUrl,
		@JsonFormat(pattern = "HH:mm", timezone = "UTC")
		LocalDateTime answeredAt
	) {
	}
}
