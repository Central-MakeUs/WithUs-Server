package com.herethere.withus.couple.dto.request;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record CoupleInitializeRequest(
	@Schema(description = "선택한 기본 키워드 ID 리스트 (없으면 빈 리스트 [])", example = "[1, 2]")
	@NotNull
	List<Long> defaultKeywordIds,

	@Schema(description = "직접 입력한 커스텀 키워드 리스트 (없으면 빈 리스트 [])", example = "['산책', '맛집']")
	@NotNull
	List<String> customKeywords,

	@Schema(
		type = "string",
		pattern = "HH:mm",
		example = "21:00",
		description = "매일 질문 알림을 받을 시각 (UTC 기준, HH:mm 형식)"
	)
	@NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "UTC")
	LocalTime questionTime
) {
	@Schema(hidden = true)
	@AssertTrue(message = "키워드는 합쳐서 1개 이상 3개 이하로 선택해주세요.")
	public boolean isValidSize() {
		int total = (defaultKeywordIds == null ? 0 : defaultKeywordIds.size()) + (customKeywords == null ? 0 :
			customKeywords.size());
		return total >= 1 && total <= 3;
	}
}
