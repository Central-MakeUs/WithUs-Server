package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

public interface ArchiveDayView {

	LocalDate getDate();

	String getArchiveType();

	Long getSourceId();

	String getMeImageKey();

	String getPartnerImageKey();
}
