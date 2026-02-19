package com.herethere.withus.user.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.fixture.UserFixture;

@DisplayName("User 엔티티 단위 테스트")
class UserTest {

	@Test
	@DisplayName("이미지 키를 포함해 updateProfile 호출 시 닉네임, 생일, 이미지 키가 모두 변경된다")
	void updateProfile_withImage_updatesAllFields() {
		User user = UserFixture.activeInitializedUserWithId(1L);

		user.updateProfile("새닉네임", LocalDate.of(1996, 5, 15), "images/origin/new.jpg");

		assertThat(user.getNickname()).isEqualTo("새닉네임");
		assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1996, 5, 15));
		assertThat(user.getProfileImageKey()).isEqualTo("images/origin/new.jpg");
	}

	@Test
	@DisplayName("이미지 없이 updateProfile 호출 시 닉네임, 생일만 변경되고 기존 이미지 키는 유지된다")
	void updateProfile_withoutImage_updatesNicknameAndBirthdayOnly() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		String originalImageKey = user.getProfileImageKey();

		user.updateProfile("새닉네임", LocalDate.of(1996, 5, 15));

		assertThat(user.getNickname()).isEqualTo("새닉네임");
		assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1996, 5, 15));
		assertThat(user.getProfileImageKey()).isEqualTo(originalImageKey);
	}

	@Test
	@DisplayName("온보딩 완료 시 닉네임, 생일, 이미지 키가 저장되고 isInitialized 가 true 로 변경된다")
	void completeOnboarding_setsAllFieldsAndMarksInitialized() {
		User user = UserFixture.activeUninitializedUserWithId(2L);
		assertThat(user.isInitialized()).isFalse();

		user.completeOnboarding("온보딩완료", LocalDate.of(1998, 3, 20), "images/origin/profile.jpg");

		assertThat(user.isInitialized()).isTrue();
		assertThat(user.getNickname()).isEqualTo("온보딩완료");
		assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1998, 3, 20));
		assertThat(user.getProfileImageKey()).isEqualTo("images/origin/profile.jpg");
	}

	@Test
	@DisplayName("탈퇴 처리 시 상태가 DELETED 로 변경되고 닉네임 익명화, 개인정보가 삭제된다")
	void withdraw_anonymizesUserData() {
		User user = UserFixture.activeInitializedUserWithId(1L);

		user.withdraw();

		assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
		assertThat(user.getNickname()).isEqualTo("알 수 없음");
		assertThat(user.getBirthday()).isNull();
		assertThat(user.getProfileImageKey()).isNull();
		assertThat(user.getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("이미 탈퇴한 사용자가 withdraw 를 재호출하면 ConflictException 이 발생한다")
	void withdraw_throwsConflictException_whenAlreadyDeleted() {
		User user = UserFixture.deletedUserWithId(1L);

		assertThatThrownBy(user::withdraw)
			.isInstanceOf(ConflictException.class);
	}

	@Test
	@DisplayName("getJoinDate 는 createdAt 을 LocalDate 로 변환해 반환한다")
	void getJoinDate_returnsCreatedAtAsLocalDate() {
		User user = UserFixture.activeInitializedUserWithId(1L);

		LocalDate joinDate = user.getJoinDate();

		assertThat(joinDate).isEqualTo(LocalDate.of(2024, 1, 1));
	}
}
