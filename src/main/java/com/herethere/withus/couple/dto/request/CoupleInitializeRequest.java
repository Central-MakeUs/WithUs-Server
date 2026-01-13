package com.herethere.withus.couple.dto.request;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;

public record CoupleInitializeRequest(List<Long> selectedKeywordIds, List<String> customKeywords) {
	@AssertTrue(message = "키워드는 합쳐서 1개 이상 3개 이하로 선택해주세요.")
	private boolean isValidSize() {
		int total = (selectedKeywordIds == null ? 0 : selectedKeywordIds.size()) + (customKeywords == null ? 0 :
			customKeywords.size());
		return total >= 1 && total <= 3;
	}
}
