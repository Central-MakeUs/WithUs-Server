package com.herethere.withus.keyword.dto.response;

import java.util.List;

public record CoupleKeywordsResponse(List<CoupleKeywordInfo> coupleKeywords) {
	public record CoupleKeywordInfo(
		Long keywordId, Long coupleKeywordId, String content
	) {
	}
}
