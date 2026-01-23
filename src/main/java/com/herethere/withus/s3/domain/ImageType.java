package com.herethere.withus.s3.domain;

import lombok.Getter;

@Getter
public enum ImageType {
	PROFILE("profile"),
	MEMORY("memory"),
	FOUR_CUT("four-cut");

	private final String path;

	ImageType(String path) {
		this.path = path;
	}
}
