package com.herethere.withus.keyword.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "우리 커플이 설정한 키워드 목록 응답")
public record CoupleKeywordsResponse(
	@Schema(description = "커플 선택 키워드 리스트")
	List<CoupleKeywordInfo> coupleKeywords
) {
	@Schema(description = "커플 키워드 개별 정보")
	public record CoupleKeywordInfo(
		@Schema(description = "키워드 ID", example = "1")
		Long keywordId,

		@Schema(description = "커플-키워드 매핑 고유 ID (조회 시 사용)", example = "101")
		Long coupleKeywordId,

		@Schema(description = "키워드 내용", example = "밥타임")
		String content
	) {
	}
}
