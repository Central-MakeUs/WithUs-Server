package com.herethere.withus.auth.domain;

import static com.herethere.withus.common.exception.ErrorCode.*;

import com.herethere.withus.common.exception.NotFoundException;

public enum OAuthProviderType {
	KAKAO,
	GOOGLE,
	APPLE;

	public static OAuthProviderType from(String value) {
		try {
			return OAuthProviderType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new NotFoundException(PROVIDER_NOT_FOUND);
		}
	}
}
