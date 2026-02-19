package com.herethere.withus.fixture;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserStatus;

public class UserFixture {

	public static User activeInitializedUser() {
		return User.builder()
			.nickname("테스터")
			.provider(OAuthProviderType.KAKAO)
			.providerId("kakao123")
			.birthday(LocalDate.of(1995, 1, 1))
			.isInitialized(true)
			.profileImageKey("images/origin/users/1/profile/abc.jpg")
			.userStatus(UserStatus.ACTIVE)
			.build();
	}

	public static User activeInitializedUserWithId(Long id) {
		User user = activeInitializedUser();
		ReflectionTestUtils.setField(user, "id", id);
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2024, 1, 1, 0, 0));
		return user;
	}

	public static User activeUninitializedUser() {
		return User.builder()
			.nickname("미설정유저")
			.provider(OAuthProviderType.KAKAO)
			.providerId("kakao456")
			.isInitialized(false)
			.userStatus(UserStatus.ACTIVE)
			.build();
	}

	public static User activeUninitializedUserWithId(Long id) {
		User user = activeUninitializedUser();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}

	public static User deletedUser() {
		return User.builder()
			.nickname("알 수 없음")
			.provider(OAuthProviderType.KAKAO)
			.providerId("kakao789")
			.isInitialized(true)
			.userStatus(UserStatus.DELETED)
			.deletedAt(LocalDateTime.of(2024, 6, 1, 12, 0))
			.build();
	}

	public static User deletedUserWithId(Long id) {
		User user = deletedUser();
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}
}
