package com.herethere.withus.s3.domain;

public enum FileCategory {
	ORIGIN("images/origin/"),
	THUMBNAIL("images/thumbnail/");

	private final String prefix;

	FileCategory(String prefix) {
		this.prefix = prefix;
	}

	public String addPrefix(String key) {
		return prefix + key;
	}
}
