package com.herethere.withus.archive.dto.request;

import java.time.LocalDate;

import com.herethere.withus.archive.enums.ArchiveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "보관 사진 삭제 요청")
public record ArchiveDeleteRequest(
	@Schema(description = "삭제할 사진의 타입 (QUESTION, KEYWORD)", example = "QUESTION")
	@NotNull ArchiveType archiveType,

	@Schema(description = "삭제할 항목의 고유 ID (`/me/couple/archives` 응답의 ImageInfo.id)", example = "12")
	@NotNull Long id,

	@Schema(description = "삭제할 사진의 날짜 (`/me/couple/archives` 응답의 ArchiveInfo.date)", example = "2026-01-28")
	@NotNull LocalDate date
) {
}
