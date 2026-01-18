package com.herethere.withus.keyword.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

public record TodayKeywordResponse(Long coupleKeywordId, String question, MemberInfo myInfo, MemberInfo partnerInfo) {
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
