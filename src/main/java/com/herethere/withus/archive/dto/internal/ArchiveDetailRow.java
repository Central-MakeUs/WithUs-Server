package com.herethere.withus.archive.dto.internal;

import java.time.LocalDateTime;

public record ArchiveDetailRow(
	String archiveType,
	Long sourceId,
	String content,
	String meImageKey,
	LocalDateTime meAnsweredAt,
	String partnerImageKey,
	LocalDateTime partnerAnsweredAt
) {
}
