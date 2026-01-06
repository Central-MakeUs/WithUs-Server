package com.herethere.withus.auth.oauthclient;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.domain.OAuthProviderType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthClientFactory {
	private final Map<String, OAuthClient> oauthClients;

	public OAuthClient getOAuthClient(OAuthProviderType oauthProviderType) {
		return oauthClients.get(oauthProviderType.name().toLowerCase());
	}
}
