package com.herethere.withus.auth.externalapi;

import static org.springframework.http.MediaType.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.herethere.withus.auth.dto.request.AppleRevokeRequest;
import com.herethere.withus.auth.dto.request.AppleTokenRequest;
import com.herethere.withus.auth.dto.response.ApplePublicKeyResponse;
import com.herethere.withus.auth.dto.response.AppleTokenResponse;

@FeignClient(name = "appleClient", url = "https://appleid.apple.com/auth", configuration = FeignConfig.class)
public interface AppleApiClient {
	@GetMapping(value = "/keys")
	ApplePublicKeyResponse findAppleAuthPublicKeys();

	@PostMapping(value = "/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
	AppleTokenResponse findAppleToken(@RequestBody AppleTokenRequest request);

	@PostMapping(value = "/revoke", consumes = APPLICATION_FORM_URLENCODED_VALUE)
	void revoke(@RequestBody AppleRevokeRequest request);
}
