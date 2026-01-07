package com.herethere.withus.auth.service;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.oauthclient.OAuthClient;
import com.herethere.withus.auth.oauthclient.OAuthClientFactory;
import com.herethere.withus.common.jwt.JwtUtil;
import com.herethere.withus.common.jwt.dto.JwtPayload;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String PREFIX_GUEST = "GUEST_";
	private final OAuthClientFactory oauthClientFactory;
	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;

	@Transactional
	public LoginResponse login(LoginRequest request, OAuthProviderType provider) {
		OAuthClient oauthClient = oauthClientFactory.getOAuthClient(provider);
		OAuthUserInfo userInfo = oauthClient.getUserInfo(request.oauthToken());

		User user = userRepository.findByProviderAndProviderId(provider, userInfo.oauthUserId())
			.orElseGet(() -> userRepository.save(
				User.builder()
					.provider(provider)
					.providerId(userInfo.oauthUserId())
					.nickname(PREFIX_GUEST + userInfo.oauthUserId())
					.isInitialized(false)
					.build()));

		JwtPayload jwtPayload = new JwtPayload(user.getId(), user.getNickname());
		String jwt = jwtUtil.createToken(jwtPayload);
		return new LoginResponse(jwt, user.isInitialized());
	}

	@Transactional
	public LoginResponse generateTempToken() {
		User user = userRepository.findByProviderAndProviderId(OAuthProviderType.KAKAO, 1234L)
			.orElseGet(() -> userRepository.save(
				User.builder()
					.provider(OAuthProviderType.KAKAO)
					.providerId(1234L)
					.nickname("tempUser")
					.isInitialized(false)
					.build()));
		JwtPayload jwtPayload = new JwtPayload(user.getId(), user.getNickname());
		String jwt = jwtUtil.createToken(jwtPayload);
		return new LoginResponse(jwt, user.isInitialized());
	}
}
