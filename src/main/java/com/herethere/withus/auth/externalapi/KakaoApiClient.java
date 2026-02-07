package com.herethere.withus.auth.externalapi;

import static org.springframework.http.MediaType.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.herethere.withus.auth.dto.internal.KakaoUserInfo;
import com.herethere.withus.auth.dto.request.KakaoRevokeRequest;

@FeignClient(name = "kakaoApi", url = "https://kapi.kakao.com", configuration = FeignConfig.class)
public interface KakaoApiClient {
	@GetMapping("/v2/user/me")
	KakaoUserInfo getUser(
		@RequestHeader("Authorization") String accessToken
	);

	@PostMapping(value = "/v1/user/unlink", consumes = APPLICATION_FORM_URLENCODED_VALUE)
	void revoke(
		@RequestHeader("Authorization") String adminKey,
		@RequestBody KakaoRevokeRequest request);
}
