package com.herethere.withus.user.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserOnboardingRequest(
	@Schema(description = "설정할 닉네임 (2~8자)", example = "하늘이")
	@NotBlank(message = "닉네임은 필수 항목입니다.")
	@Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
	String nickname,

	@Schema(description = "생일", example = "2000-01-02")
	@NotNull(message = "생일은 필수 항목입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd")
	LocalDate birthday,

	@Schema(description = "선택한 기본 키워드 ID 리스트 (없으면 빈 리스트 [])", example = "[1, 2]")
	@NotNull
	List<Long> defaultKeywordIds,

	@Schema(description = "직접 입력한 커스텀 키워드 리스트 (없으면 빈 리스트 [])", example = "['산책', '맛집']")
	@NotNull
	List<String> customKeywords,

	@Schema(
		description = "S3 업로드 후 받은 이미지 객체 키 (presigned-url 응답의 imageKey)",
		example = "profiles/1/uuid_filename.jpg",
		nullable = true
	)
	String imageKey) {
	@Schema(hidden = true)
	@AssertTrue(message = "키워드는 합쳐서 1개 이상 2개 이하로 선택해주세요.")
	public boolean isValidSize() {
		int total = (defaultKeywordIds == null ? 0 : defaultKeywordIds.size()) + (customKeywords == null ? 0 :
			customKeywords.size());
		return total >= 1 && total <= 2;
	}
}
