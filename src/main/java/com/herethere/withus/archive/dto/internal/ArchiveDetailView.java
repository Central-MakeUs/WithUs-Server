package com.herethere.withus.archive.dto.internal;

import java.time.LocalDateTime;

public interface ArchiveDetailView {
	String getArchiveType();

	Long getSourceId();

	String getContent();

	String getMeImageKey();

	LocalDateTime getMeAnsweredAt();

	String getPartnerImageKey();

	LocalDateTime getPartnerAnsweredAt();
}
