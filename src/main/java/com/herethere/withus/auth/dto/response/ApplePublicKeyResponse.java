package com.herethere.withus.auth.dto.response;

import java.util.List;

public record ApplePublicKeyResponse(List<AppleKeyInfo> keys) {

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
