package com.herethere.withus.auth.service;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.oauthclient.OAuthClient;
import com.herethere.withus.auth.oauthclient.OAuthClientFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final OAuthClientFactory oauthClientFactory;

	public LoginResponse login(LoginRequest request, OAuthProviderType provider) {
		OAuthClient oauthClient = oauthClientFactory.getOAuthClient(provider);
		OAuthUserInfo userInfo = oauthClient.getUserInfo(request.oauthToken());
		// TODO:추후작업
		return null;
	}
}
