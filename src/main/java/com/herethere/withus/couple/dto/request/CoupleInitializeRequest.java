package com.herethere.withus.couple.dto.request;

import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record CoupleInitializeRequest(@NotNull List<Long> defaultKeywordIds, @NotNull List<String> customKeywords,
									  @NotNull LocalTime questionTime) {
	@AssertTrue(message = "키워드는 합쳐서 1개 이상 3개 이하로 선택해주세요.")
	private boolean isValidSize() {
		int total = (defaultKeywordIds == null ? 0 : defaultKeywordIds.size()) + (customKeywords == null ? 0 :
			customKeywords.size());
		return total >= 1 && total <= 3;
	}
}
