package com.herethere.withus.user.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.security.SecureRandom;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.service.OnboardingManager;
import com.herethere.withus.keyword.service.KeywordService;
import com.herethere.withus.notification.dto.internal.FcmNotificationEvent;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.dto.request.UserOnboardingRequest;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.OnboardingStatusResponse;
import com.herethere.withus.user.dto.response.UserOnboardingResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;
import com.herethere.withus.user.repository.InviteCodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final SecureRandom secureRandom = new SecureRandom();
	private final UserContextService userContextService;
	private final OnboardingManager onboardingManager;
	private final KeywordService keywordService;
	private final S3Service s3Service;
	private final InviteCodeRepository inviteCodeRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public UserUpdateResponse updateUserProfile(UserUpdateRequest userUpdateRequest) {
		User user = userContextService.getInitializedUser();
		user.updateProfile(userUpdateRequest.nickname(), userUpdateRequest.birthday(), userUpdateRequest.imageKey());
		return new UserUpdateResponse(user.getId(), user.getNickname(), user.getBirthday(), user.getProfileImageKey());
	}

	@Transactional(readOnly = true)
	public UserUpdateResponse getUserProfile() {
		User user = userContextService.getInitializedUser();
		return new UserUpdateResponse(user.getId(), user.getNickname(), user.getBirthday(), user.getProfileImageKey());
	}

	@Transactional
	public InvitationCodeResponse generateInvitationCode() {
		User user = userContextService.getInitializedUser();
		if (user.getCouple() != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		// inviteCode가 이미 존재하면 가져오고, 없으면 중복 확인해서 생성
		InviteCode inviteCode = inviteCodeRepository.findByUser(user)
			.orElseGet(
				() -> {
					return createNewInviteCode(user);
				}
			);
		return new InvitationCodeResponse(inviteCode.getCode());
	}

	@Transactional(readOnly = true)
	public OnboardingStatusResponse getOnboardingStatus() {
		User user = userContextService.getCurrentUser();
		return new OnboardingStatusResponse(onboardingManager.getStatus(user));
	}

	@Transactional(readOnly = true)
	public void pokeUser(Long userId) {
		User user = userContextService.getCoupledUser();
		User partner = user.getPartner();

		if (!partner.getId().equals(userId)) {
			throw new BadRequestException(NOT_YOUR_PARTNER);
		}

		// TODO: 캐시를 사용한 찌르기 스팸 방지 로직 추가

		eventPublisher.publishEvent(FcmNotificationEvent.createPokeEvent(user, partner));
	}

	@Transactional
	public UserOnboardingResponse onboardUser(UserOnboardingRequest request) {
		User user = userContextService.getCurrentUser();
		if (user.isInitialized()) {
			throw new ConflictException(USER_ALREADY_INITIALIZED); // TODO: 추후 기획에 따라 빠질 수 있음
		}

		String finalImageKey = s3Service.processImagePublish(request.imageKey(), user.getId(), ImageType.PROFILE);

		user.completeOnboarding(request.nickname(), request.birthday(), finalImageKey);

		return new UserOnboardingResponse(user.getId(), user.getNickname(), user.getProfileImageKey());
	}

	private InviteCode createNewInviteCode(User user) {
		for (int i = 0; i < 20; i++) {
			String code = generate8DigitCode();
			if (!inviteCodeRepository.existsByCode(code)) {
				return inviteCodeRepository.save(
					InviteCode.builder().user(user).code(code).build());
			}
		}
		throw new NotFoundException(CODE_NOT_FOUND);
	}

	private String generate8DigitCode() {
		int number = secureRandom.nextInt(90_000_000) + 10_000_000;
		return String.valueOf(number);
	}
}
