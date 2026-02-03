package com.herethere.withus.auth.dto.response;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.util.List;

import com.herethere.withus.common.exception.AuthException;

public record ApplePublicKeyResponse(List<AppleKeyInfo> keys) {

	public AppleKeyInfo getMatchedKey(String kid, String alg) {
		return keys.stream()
			.filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
			.findAny()
			.orElseThrow(() -> new AuthException(INVALID_JWT_TOKEN));
	}

	public record AppleKeyInfo(
		String kty,
		String kid,
		String use,
		String alg,
		String n,
		String e
	) {
	}
}
