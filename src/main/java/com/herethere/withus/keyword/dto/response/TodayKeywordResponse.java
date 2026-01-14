package com.herethere.withus.keyword.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

public record TodayKeywordResponse(Long coupleKeywordId, String question, MemberInfo myInfo, MemberInfo partnerInfo) {
	@Builder
	public record MemberInfo(
		String name,
		String profileImageUrl,
		String questionImageUrl,
		LocalDateTime answeredAt
	) {
	}
}
