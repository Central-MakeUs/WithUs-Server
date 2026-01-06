package com.herethere.withus.auth.oauthclient;

import com.herethere.withus.auth.dto.internal.OAuthUserInfo;

public interface OAuthClient {
	OAuthUserInfo getUserInfo(String oauthToken);
}
