package com.herethere.withus.auth.oauthclient;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.dto.internal.KakaoUserInfo;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.externalapi.KakaoApiClient;

import lombok.RequiredArgsConstructor;

@Service("kakao")
@RequiredArgsConstructor
public class KakaoClient implements OAuthClient {
	private final KakaoApiClient kakaoApiClient;

	@Override
	public OAuthUserInfo getUserInfo(String oauthToken) {
		oauthToken = "Bearer " + oauthToken;
		KakaoUserInfo userInfo = kakaoApiClient.getUser(oauthToken);
		return new OAuthUserInfo(userInfo.id());
	}
}
