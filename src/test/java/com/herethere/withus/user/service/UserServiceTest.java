package com.herethere.withus.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.herethere.withus.auth.oauthclient.OAuthClient;
import com.herethere.withus.auth.oauthclient.OAuthClientFactory;
import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.OnboardingStatus;
import com.herethere.withus.couple.service.OnboardingManager;
import com.herethere.withus.fixture.CoupleFixture;
import com.herethere.withus.fixture.UserFixture;
import com.herethere.withus.keyword.service.KeywordService;
import com.herethere.withus.notification.dto.internal.CoupleNotificationEvent;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserStatus;
import com.herethere.withus.user.dto.request.UserOnboardingRequest;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.OnboardingStatusResponse;
import com.herethere.withus.user.dto.response.UserOnboardingResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;
import com.herethere.withus.user.repository.InviteCodeRepository;
import com.herethere.withus.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

	@Mock
	private AppContextService appContextService;
	@Mock
	private OnboardingManager onboardingManager;
	@Mock
	private KeywordService keywordService;
	@Mock
	private S3Service s3Service;
	@Mock
	private InviteCodeRepository inviteCodeRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	@Mock
	private OAuthClientFactory oauthClientFactory;
	@Mock
	private OAuthClient oauthClient;
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	// ─── updateUserProfile ────────────────────────────────────────────────────

	@Test
	@DisplayName("isImageUpdated 가 true 이면 S3 에서 이미지를 이동시키고 새 이미지 키로 프로필을 업데이트한다")
	void updateUserProfile_withImageUpdated_updatesProfileWithNewImageKey() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);

		String newImageKey = "images/origin/users/1/profile/new.jpg";
		given(s3Service.processImagePublish(any(), eq(1L), eq(ImageType.PROFILE))).willReturn(newImageKey);
		given(s3Service.createOriginImageUrl(newImageKey)).willReturn("https://s3.example.com/new.jpg");

		UserUpdateRequest request = new UserUpdateRequest("새닉네임", LocalDate.of(1996, 5, 15),
			"temp/origin/users/1/profile/new.jpg", true);

		UserUpdateResponse response = userService.updateUserProfile(request);

		assertThat(response.nickname()).isEqualTo("새닉네임");
		assertThat(response.birthday()).isEqualTo(LocalDate.of(1996, 5, 15));
		assertThat(response.profileImageUrl()).isEqualTo("https://s3.example.com/new.jpg");
	}

	@Test
	@DisplayName("isImageUpdated 가 false 이면 S3 처리 없이 닉네임과 생일만 업데이트한다")
	void updateUserProfile_withImageNotUpdated_skipsS3ProcessAndUsesExistingKey() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(s3Service.createOriginImageUrl(user.getProfileImageKey()))
			.willReturn("https://s3.example.com/existing.jpg");

		UserUpdateRequest request = new UserUpdateRequest("새닉네임", LocalDate.of(1996, 5, 15), null, false);

		UserUpdateResponse response = userService.updateUserProfile(request);

		assertThat(response.nickname()).isEqualTo("새닉네임");
		assertThat(response.birthday()).isEqualTo(LocalDate.of(1996, 5, 15));
		verify(s3Service, never()).processImagePublish(any(), any(), any());
	}

	// ─── getUserProfile ───────────────────────────────────────────────────────

	@Test
	@DisplayName("현재 로그인한 사용자의 프로필 정보를 반환한다")
	void getUserProfile_returnsCurrentUserProfile() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(s3Service.createOriginImageUrl(user.getProfileImageKey()))
			.willReturn("https://s3.example.com/profile.jpg");

		UserUpdateResponse response = userService.getUserProfile();

		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.nickname()).isEqualTo(user.getNickname());
		assertThat(response.joinDate()).isEqualTo(LocalDate.of(2024, 1, 1));
	}

	// ─── generateInvitationCode ───────────────────────────────────────────────

	@Test
	@DisplayName("이미 커플 관계인 사용자가 초대 코드를 요청하면 ConflictException 이 발생한다")
	void generateInvitationCode_throwsConflictException_whenAlreadyInCouple() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		User partner = UserFixture.activeInitializedUserWithId(2L);
		Couple couple = CoupleFixture.activeCoupleWithId(10L, user, partner);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.findActiveCouple(user)).willReturn(Optional.of(couple));

		assertThatThrownBy(() -> userService.generateInvitationCode())
			.isInstanceOf(ConflictException.class);
	}

	@Test
	@DisplayName("이미 발급된 초대 코드가 있으면 새로 생성하지 않고 기존 코드를 반환한다")
	void generateInvitationCode_returnsExistingCode_whenAlreadyGenerated() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		InviteCode existingCode = InviteCode.builder().user(user).code("12345678").build();
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.findActiveCouple(user)).willReturn(Optional.empty());
		given(inviteCodeRepository.findByUser(user)).willReturn(Optional.of(existingCode));

		InvitationCodeResponse response = userService.generateInvitationCode();

		assertThat(response.invitationCode()).isEqualTo("12345678");
		verify(inviteCodeRepository, never()).save(any());
	}

	@Test
	@DisplayName("발급된 초대 코드가 없으면 8자리 코드를 새로 생성해 저장하고 반환한다")
	void generateInvitationCode_createsAndSavesNewCode_whenNoneExists() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		InviteCode savedCode = InviteCode.builder().user(user).code("87654321").build();
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.findActiveCouple(user)).willReturn(Optional.empty());
		given(inviteCodeRepository.findByUser(user)).willReturn(Optional.empty());
		given(inviteCodeRepository.existsByCode(any())).willReturn(false);
		given(inviteCodeRepository.save(any())).willReturn(savedCode);

		InvitationCodeResponse response = userService.generateInvitationCode();

		assertThat(response.invitationCode()).isEqualTo("87654321");
		verify(inviteCodeRepository).save(any(InviteCode.class));
	}

	@Test
	@DisplayName("20회 재시도 동안 중복이 계속 발생하면 NotFoundException 이 발생한다")
	void generateInvitationCode_throwsNotFoundException_whenAllRetriesExhausted() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.findActiveCouple(user)).willReturn(Optional.empty());
		given(inviteCodeRepository.findByUser(user)).willReturn(Optional.empty());
		given(inviteCodeRepository.existsByCode(any())).willReturn(true);

		assertThatThrownBy(() -> userService.generateInvitationCode())
			.isInstanceOf(NotFoundException.class);
	}

	// ─── getOnboardingStatus ──────────────────────────────────────────────────

	@Test
	@DisplayName("현재 사용자의 온보딩 단계를 반환한다")
	void getOnboardingStatus_returnsOnboardingStatus() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getActiveUser()).willReturn(user);
		given(onboardingManager.getStatus(user)).willReturn(OnboardingStatus.COMPLETED);

		OnboardingStatusResponse response = userService.getOnboardingStatus();

		assertThat(response.status()).isEqualTo(OnboardingStatus.COMPLETED);
	}

	// ─── pokeUser ─────────────────────────────────────────────────────────────

	@Test
	@DisplayName("유효한 파트너 ID 로 찌르기 요청 시 POKE 이벤트를 발행한다")
	void pokeUser_publishesPokeEvent_whenPartnerIdMatches() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		User partner = UserFixture.activeInitializedUserWithId(2L);
		Couple couple = CoupleFixture.activeCoupleWithId(10L, user, partner);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.getActiveCoupleRequired(user)).willReturn(couple);

		userService.pokeUser(2L);

		ArgumentCaptor<CoupleNotificationEvent> captor =
			ArgumentCaptor.forClass(CoupleNotificationEvent.class);
		verify(eventPublisher).publishEvent(captor.capture());
		assertThat(captor.getValue().senderId()).isEqualTo(1L);
		assertThat(captor.getValue().receiverId()).isEqualTo(2L);
	}

	@Test
	@DisplayName("요청한 userId 가 실제 파트너 ID 와 다르면 BadRequestException 이 발생한다")
	void pokeUser_throwsBadRequestException_whenNotPartner() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		User partner = UserFixture.activeInitializedUserWithId(2L);
		Couple couple = CoupleFixture.activeCoupleWithId(10L, user, partner);
		given(appContextService.getInitializedAndActiveUser()).willReturn(user);
		given(appContextService.getActiveCoupleRequired(user)).willReturn(couple);

		assertThatThrownBy(() -> userService.pokeUser(99L))
			.isInstanceOf(BadRequestException.class);
	}

	// ─── onboardUser ──────────────────────────────────────────────────────────

	@Test
	@DisplayName("미설정 사용자가 온보딩 요청 시 프로필이 저장되고 isInitialized 가 true 로 변경된다")
	void onboardUser_completesOnboardingAndReturnsResponse() {
		User user = UserFixture.activeUninitializedUserWithId(1L);
		given(appContextService.getActiveUser()).willReturn(user);

		String finalImageKey = "images/origin/users/1/profile/abc.jpg";
		given(s3Service.processImagePublish(any(), eq(1L), eq(ImageType.PROFILE))).willReturn(finalImageKey);
		given(s3Service.createOriginImageUrl(finalImageKey)).willReturn("https://s3.example.com/abc.jpg");

		UserOnboardingRequest request = new UserOnboardingRequest(
			"새유저", LocalDate.of(2000, 6, 1), "temp/origin/users/1/profile/abc.jpg");

		UserOnboardingResponse response = userService.onboardUser(request);

		assertThat(response.userId()).isEqualTo(1L);
		assertThat(response.nickname()).isEqualTo("새유저");
		assertThat(response.profileImageUrl()).isEqualTo("https://s3.example.com/abc.jpg");
		assertThat(user.isInitialized()).isTrue();
	}

	@Test
	@DisplayName("이미 초기 설정이 완료된 사용자가 온보딩 요청 시 ConflictException 이 발생한다")
	void onboardUser_throwsConflictException_whenAlreadyInitialized() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getActiveUser()).willReturn(user);

		UserOnboardingRequest request = new UserOnboardingRequest(
			"닉네임", LocalDate.of(2000, 1, 1), null);

		assertThatThrownBy(() -> userService.onboardUser(request))
			.isInstanceOf(ConflictException.class);
	}

	// ─── withdrawUser ─────────────────────────────────────────────────────────

	@Test
	@DisplayName("탈퇴 시 OAuth 연동 해제를 먼저 호출하고 사용자 상태를 DELETED 로 변경한다")
	void withdrawUser_callsOAuthWithdrawThenWithdrawsUser() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(appContextService.getActiveUser()).willReturn(user);
		given(oauthClientFactory.getOAuthClient(user.getProvider())).willReturn(oauthClient);

		userService.withdrawUser();

		verify(oauthClient).withdrawUser(user);
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.DELETED);
	}

	// ─── getNickname ──────────────────────────────────────────────────────────

	@Test
	@DisplayName("사용자가 존재하면 해당 사용자의 닉네임을 반환한다")
	void getNickname_returnsNickname_whenUserFound() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		String nickname = userService.getNickname(1L);

		assertThat(nickname).isEqualTo(user.getNickname());
	}

	@Test
	@DisplayName("사용자가 존재하지 않으면 기본값 '상대방' 을 반환한다")
	void getNickname_returnsFallback_whenUserNotFound() {
		given(userRepository.findById(99L)).willReturn(Optional.empty());

		String nickname = userService.getNickname(99L);

		assertThat(nickname).isEqualTo("상대방");
	}

	// ─── isActive ─────────────────────────────────────────────────────────────

	@Test
	@DisplayName("ACTIVE 상태인 사용자의 경우 true 를 반환한다")
	void isActive_returnsTrue_whenUserIsActive() {
		User user = UserFixture.activeInitializedUserWithId(1L);
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		assertThat(userService.isActive(1L)).isTrue();
	}

	@Test
	@DisplayName("DELETED 상태인 사용자의 경우 false 를 반환한다")
	void isActive_returnsFalse_whenUserIsDeleted() {
		User user = UserFixture.deletedUserWithId(1L);
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		assertThat(userService.isActive(1L)).isFalse();
	}

	@Test
	@DisplayName("사용자가 존재하지 않는 경우 false 를 반환한다")
	void isActive_returnsFalse_whenUserNotFound() {
		given(userRepository.findById(99L)).willReturn(Optional.empty());

		assertThat(userService.isActive(99L)).isFalse();
	}
}
