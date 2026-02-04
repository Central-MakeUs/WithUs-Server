package com.herethere.withus.auth.service;

import org.springframework.stereotype.Service;

import com.herethere.withus.auth.domain.AppleRefreshToken;
import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.response.LoginResponse;
import com.herethere.withus.auth.oauthclient.OAuthClient;
import com.herethere.withus.auth.oauthclient.OAuthClientFactory;
import com.herethere.withus.auth.repository.AppleRefreshTokenRepository;
import com.herethere.withus.common.jwt.JwtUtil;
import com.herethere.withus.common.jwt.dto.JwtPayload;
import com.herethere.withus.couple.service.OnboardingManager;
import com.herethere.withus.notification.service.FcmTokenManager;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserStatus;
import com.herethere.withus.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private static final String PREFIX_GUEST = "GUEST_";
	private final OAuthClientFactory oauthClientFactory;
	private final FcmTokenManager fcmTokenManager;
	private final OnboardingManager onboardingManager;
	private final UserRepository userRepository;
	private final AppleRefreshTokenRepository appleRefreshTokenRepository;
	private final JwtUtil jwtUtil;

	@Transactional
	public LoginResponse login(LoginRequest request, OAuthProviderType provider) {
		OAuthClient oauthClient = oauthClientFactory.getOAuthClient(provider);
		OAuthUserInfo userInfo = oauthClient.getUserInfo(request.oauthToken(), request.authorizationCode());

		User user = userRepository.findByProviderAndProviderId(provider, userInfo.oauthUserId())
			.orElseGet(() -> userRepository.save(
				User.builder()
					.provider(provider)
					.providerId(userInfo.oauthUserId())
					.nickname(PREFIX_GUEST + userInfo.oauthUserId())
					.isInitialized(false)
					.userStatus(UserStatus.ACTIVE)
					.build()));
		// refreshToken 저장
		if (userInfo.refreshToken() != null) {
			appleRefreshTokenRepository.save(
				AppleRefreshToken.builder().user(user).refreshToken(userInfo.refreshToken()).build());
		}
		// FCM 토큰 저장
		fcmTokenManager.saveOrUpdateToken(user, request.fcmToken());

		JwtPayload jwtPayload = new JwtPayload(user.getId(), user.getNickname());
		String jwt = jwtUtil.createToken(jwtPayload);
		return new LoginResponse(jwt, onboardingManager.getStatus(user));
	}

	@Transactional
	public LoginResponse generateTempToken(String id, String fcmToken) {
		User user = userRepository.findByProviderAndProviderId(OAuthProviderType.KAKAO, id)
			.orElseGet(() -> userRepository.save(
				User.builder()
					.provider(OAuthProviderType.KAKAO)
					.providerId(id)
					.nickname("tempUser")
					.userStatus(UserStatus.ACTIVE)
					.isInitialized(false)
					.build()));

		fcmTokenManager.saveOrUpdateToken(user, fcmToken);

		JwtPayload jwtPayload = new JwtPayload(user.getId(), user.getNickname());
		String jwt = jwtUtil.createToken(jwtPayload);
		return new LoginResponse(jwt, onboardingManager.getStatus(user));
	}
}
