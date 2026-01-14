package com.herethere.withus.couple.dto.request;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record CoupleInitializeRequest(
	@NotNull
	List<Long> defaultKeywordIds,

	@NotNull
	List<String> customKeywords,

	@NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
	LocalTime questionTime
) {
	@AssertTrue(message = "키워드는 합쳐서 1개 이상 3개 이하로 선택해주세요.")
	public boolean isValidSize() {
		int total = (defaultKeywordIds == null ? 0 : defaultKeywordIds.size()) + (customKeywords == null ? 0 :
			customKeywords.size());
		return total >= 1 && total <= 3;
	}
}
