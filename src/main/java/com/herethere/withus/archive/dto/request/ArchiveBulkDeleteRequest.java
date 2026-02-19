package com.herethere.withus.archive.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "보관 사진 복수 삭제 요청")
public record ArchiveBulkDeleteRequest(
	@Schema(description = "삭제할 항목 목록")
	@NotEmpty @Valid List<ArchiveDeleteRequest> items
) {
}
