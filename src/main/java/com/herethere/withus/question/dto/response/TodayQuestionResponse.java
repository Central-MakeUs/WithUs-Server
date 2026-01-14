package com.herethere.withus.question.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

public record TodayQuestionResponse(Long coupleQuestionId, String question, MemberInfo myInfo, MemberInfo partnerInfo) {
	@Builder
	public record MemberInfo(
		String name,
		String profileImageUrl,
		String questionImageUrl,
		LocalDateTime answeredAt
	) {
	}
}
