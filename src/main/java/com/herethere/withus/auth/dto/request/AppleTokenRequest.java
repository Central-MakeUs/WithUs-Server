package com.herethere.withus.auth.dto.request;

public record AppleTokenRequest(
	String client_id,
	String client_secret,
	String code,
	String grant_type
) {
}
