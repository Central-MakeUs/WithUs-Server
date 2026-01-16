package com.herethere.withus.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.notification.domain.FcmToken;
import com.herethere.withus.notification.repository.FcmTokenRepository;
import com.herethere.withus.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmTokenManager {
	private final FcmTokenRepository fcmTokenRepository;

	@Transactional
	public void saveOrUpdateToken(User user, String tokenValue) {
		// 1. 해당 토큰이 이미 DB에 존재하는지 확인
		fcmTokenRepository.findByToken(tokenValue)
			.ifPresentOrElse(
				// 2. 이미 있다면 소유자 확인 후 갱신
				existingToken -> {
					if (!existingToken.getUser().equals(user)) {
						existingToken.changeUser(user);
					}
				},
				// 3. 없다면 새로 생성하여 저장
				() -> {
					FcmToken newToken = FcmToken.builder()
						.user(user)
						.token(tokenValue)
						.build();
					fcmTokenRepository.save(newToken);
				}
			);
	}
}
