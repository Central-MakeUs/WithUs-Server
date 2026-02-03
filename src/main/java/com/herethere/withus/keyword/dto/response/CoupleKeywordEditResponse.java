package com.herethere.withus.keyword.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleKeywordEditResponse(
	@Schema(description = "전체 키워드 선택 목록")
	List<KeywordSelection> keywords
) {
	public record KeywordSelection(
		@Schema(description = "기본 키워드 ID", example = "1")
		Long keywordId,

		@Schema(description = "키워드 내용", example = "산책")
		String content,

		@Schema(description = "우리 커플의 선택 여부 (true면 체크 상태)", example = "true")
		boolean isSelected
	) {
	}
}
