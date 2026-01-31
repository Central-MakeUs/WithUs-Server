package com.herethere.withus.archive.enums;

import static com.herethere.withus.common.exception.ErrorCode.*;

import com.herethere.withus.common.exception.InternalServerException;

public enum ArchiveType {
	QUESTION, KEYWORD;

	public static ArchiveType from(String value) {
		if (value == null) {
			throw new InternalServerException(ARCHIVE_TYPE_NULL);
		}

		try {
			return ArchiveType.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InternalServerException(INVALID_ARCHIVE_TYPE);
		}
	}

}
