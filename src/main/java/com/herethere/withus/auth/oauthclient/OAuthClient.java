package com.herethere.withus.auth.oauthclient;

import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.user.domain.User;

public interface OAuthClient {
	OAuthUserInfo getUserInfo(String oauthToken, String authorizationCode);

	void withdrawUser(User user);
}
