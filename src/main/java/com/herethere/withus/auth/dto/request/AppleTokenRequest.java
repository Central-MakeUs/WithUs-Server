package com.herethere.withus.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AppleTokenRequest {
	String client_id;
	String client_secret;
	String code;
	String grant_type;
}
