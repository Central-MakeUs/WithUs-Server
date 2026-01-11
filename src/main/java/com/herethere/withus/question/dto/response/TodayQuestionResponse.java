package com.herethere.withus.question.dto.response;

import java.time.LocalDateTime;

public record TodayQuestionResponse(String question, MemberInfo myInfo, MemberInfo partnerInfo) {

	public record MemberInfo(
		String name,
		String profileImageUrl,
		String questionImageUrl,
		LocalDateTime answeredAt
	) {
	}
}
