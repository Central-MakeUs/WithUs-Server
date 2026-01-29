package com.herethere.withus.archive.dto.internal;

import java.time.LocalDate;

public record ArchiveDayDto(
	LocalDate date,

	Long meUserId,
	String meImageKey,

	Long partnerUserId,
	String partnerImageKey
) {}
