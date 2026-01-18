package com.herethere.withus.keyword.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "기본 제공 키워드 목록 응답")
public record DefaultKeywordsResponse(
	@Schema(description = "기본 키워드 정보 리스트")
	List<KeywordInfo> keywordInfoList
) {
	@Schema(description = "키워드 상세 정보")
	public record KeywordInfo(
		@Schema(description = "키워드 고유 ID", example = "1")
		Long keywordId,

		@Schema(description = "키워드 내용", example = "밥타임")
		String content,

		@Schema(description = "화면 노출 순서 (낮은 숫자가 우선)", example = "1")
		Long displayOrder
	) {
	}
}
