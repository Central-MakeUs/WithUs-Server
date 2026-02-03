package com.herethere.withus.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppleTokenResponse(

	@JsonProperty(value = "access_token")
	String accessToken,

	@JsonProperty(value = "expires_in")
	Long expiresIn,

	@JsonProperty(value = "id_token")
	String idToken,

	@JsonProperty(value = "refresh_token")
	String refreshToken,

	@JsonProperty(value = "token_type")
	String tokenType,

	String error
) {
}
