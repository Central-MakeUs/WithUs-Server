package com.herethere.withus.auth.oauthclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.herethere.withus.auth.dto.internal.KakaoUserInfo;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.KakaoRevokeRequest;
import com.herethere.withus.auth.externalapi.KakaoApiClient;
import com.herethere.withus.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service("kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoClient implements OAuthClient {
	private final KakaoApiClient kakaoApiClient;
	@Value("${oauth.kakao.app.adminKey}")
	private String adminKey;

	@Override
	public OAuthUserInfo getUserInfo(String oauthToken, String authorizationCode) {
		oauthToken = "Bearer " + oauthToken;
		KakaoUserInfo userInfo = kakaoApiClient.getUser(oauthToken);
		return new OAuthUserInfo(userInfo.id(), null);
	}

	@Override
	public void withdrawUser(User user) {
		adminKey = "KakaoAK " + adminKey;
		Long providerId = Long.valueOf(user.getProviderId());
		KakaoRevokeRequest request = new KakaoRevokeRequest("user_id", providerId);
		try {
			kakaoApiClient.revoke(adminKey, request);
		} catch (Exception e) {
			log.error("Kakao 회원 탈퇴 실패 {}: {}", user.getId(), e.getMessage());
		}
	}
}
