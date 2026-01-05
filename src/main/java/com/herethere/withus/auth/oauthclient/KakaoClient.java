package com.herethere.withus.auth.oauthclient;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.dto.internal.OAuthUserInfo;

@Service("kakao")
public class KakaoClient implements OAuthClient {
	@Override
	public OAuthUserInfo getUserInfo(String oauthToken) {
		return null;
	}
}
