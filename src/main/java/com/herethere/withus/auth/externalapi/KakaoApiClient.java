package com.herethere.withus.auth.externalapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.herethere.withus.auth.dto.internal.KakaoUserInfo;

@FeignClient(name = "kakaoApi", url = "https://kapi.kakao.com")
public interface KakaoApiClient {
	@GetMapping("/v2/user/me")
	KakaoUserInfo getUser(
		@RequestHeader("Authorization") String accessToken
	);
}
