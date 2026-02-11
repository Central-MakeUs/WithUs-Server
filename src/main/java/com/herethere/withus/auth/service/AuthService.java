package com.herethere.withus.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.herethere.withus.auth.domain.AppleRefreshToken;
import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.auth.dto.internal.OAuthUserInfo;
import com.herethere.withus.auth.dto.request.LoginRequest;
import com.herethere.withus.auth.dto.request.LogoutRequest;
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
import com.herethere.withus.user.service.AppContextService;

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
	private final AppContextService appContextService;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtUtil jwtUtil;

	@Transactional
	public LoginResponse login(LoginRequest request, OAuthProviderType provider) {
		OAuthClient oauthClient = oauthClientFactory.getOAuthClient(provider);
		OAuthUserInfo userInfo = oauthClient.getUserInfo(request.oauthToken(), request.authorizationCode());

		User user = userRepository.findByProviderAndProviderIdAndUserStatus(provider, userInfo.oauthUserId(),
				UserStatus.ACTIVE)
			.orElseGet(() -> userRepository.save(
				User.builder()
					.provider(provider)
					.providerId(userInfo.oauthUserId())
					.nickname(PREFIX_GUEST + userInfo.oauthUserId())
					.isInitialized(false)
					.userStatus(UserStatus.ACTIVE)
					.build()));

		// appleRefreshToken 저장
		saveAppleRefreshToken(userInfo, user);

		// FCM 토큰 저장
		fcmTokenManager.saveOrUpdateToken(user, request.fcmToken());

		JwtPayload jwtPayload = new JwtPayload(user.getId(), user.getNickname());
		String jwt = jwtUtil.createToken(jwtPayload);

		// Refresh Token 생성 (긴 유효기간)
		String refreshToken = jwtUtil.createRefreshToken(jwtPayload);

		// Redis 저장 (Key: 유저ID, Value: 토큰)
		redisTemplate.opsForValue().set(
			"RT:" + user.getId(),
			refreshToken,
			28, TimeUnit.DAYS
		);

		return new LoginResponse(jwt, refreshToken, onboardingManager.getStatus(user));
	}

	@Transactional
	public LoginResponse generateTempToken(String id, String fcmToken) {
		User user = userRepository.findByProviderAndProviderIdAndUserStatus(OAuthProviderType.KAKAO, id,
				UserStatus.ACTIVE)
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
		return new LoginResponse(jwt, null, onboardingManager.getStatus(user));
	}

	@Transactional
	public void logout(LogoutRequest request) {
		User user = appContextService.getCurrentUser();
		fcmTokenManager.deleteByUserAndToken(user, request.fcmToken());
	}

	private void saveAppleRefreshToken(OAuthUserInfo userInfo, User user) {
		// apple refreshToken 저장
		if (userInfo.refreshToken() != null) {
			// 1. 기존 토큰 존재 여부 확인 (Optional 활용)
			AppleRefreshToken appleToken = appleRefreshTokenRepository.findByUser(user)
				.map(existingToken -> {
					// 2. 존재하면 리프레시 토큰 값만 업데이트
					existingToken.updateToken(userInfo.refreshToken());
					return existingToken;
				})
				.orElseGet(() -> {
					// 3. 존재하지 않으면 새로 빌드하여 생성
					return AppleRefreshToken.builder()
						.user(user)
						.refreshToken(userInfo.refreshToken())
						.build();
				});

			appleRefreshTokenRepository.save(appleToken);
		}
	}
}
