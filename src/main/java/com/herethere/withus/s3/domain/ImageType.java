package com.herethere.withus.s3.domain;

import lombok.Getter;

@Getter
public enum ImageType {
	PROFILE("profile"),
	ARCHIVE("archive"),
	MEMORY("memory");

	private final String path;

	ImageType(String path) {
		this.path = path;
	}
}
