package com.herethere.withus.keyword.dto.response;

import java.util.List;

public record CoupleKeywordsResponse(List<CoupleKeywords> coupleKeywords) {
	public record CoupleKeywords(
		Long keywordId, Long coupleKeywordId, String content
	) {
	}
}
