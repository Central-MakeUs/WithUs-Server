package com.herethere.withus.user.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "유저 정보 수정 및 초기 설정 요청")
public record UserUpdateRequest(
	@Schema(description = "설정할 닉네임 (2~8자)", example = "하늘이")
	@NotBlank(message = "닉네임은 필수 항목입니다.")
	@Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
	String nickname,

	@Schema(description = "생일", example = "2000-01-02")
	@NotNull(message = "생일은 필수 항목입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate birthday,

	@Schema(
		description = "S3 업로드 후 받은 이미지 객체 키 (presigned-url 응답의 imageKey)",
		example = "profiles/1/uuid_filename.jpg",
		nullable = true
	)
	String imageKey,

	@Schema(
		description = "프로필 이미지 갱신 여부",
		example = "true"
	)
	@NotNull(message = "프로필 사진 갱신 여부는 필수 항목입니다.")
	boolean isImageUpdated
) {
}
