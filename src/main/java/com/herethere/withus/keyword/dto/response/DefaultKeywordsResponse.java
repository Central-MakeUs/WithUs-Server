package com.herethere.withus.keyword.dto.response;

import java.util.List;

public record DefaultKeywordsResponse(List<KeywordInfo> keywordInfoList) {
	public record KeywordInfo(Long keywordId, String content, Long displayOrder) {
	}
}
