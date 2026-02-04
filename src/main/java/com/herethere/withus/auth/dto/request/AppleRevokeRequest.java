package com.herethere.withus.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppleRevokeRequest {
	String client_id;
	String client_secret;
	String token;
	String token_type_hint;
}
