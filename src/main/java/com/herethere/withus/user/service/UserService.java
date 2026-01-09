package com.herethere.withus.user.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.user.domain.CodeStatus;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.InvitationCodeResponse;
import com.herethere.withus.user.dto.response.UserUpdateResponse;
import com.herethere.withus.user.repository.InviteCodeRepository;
import com.herethere.withus.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private static final SecureRandom secureRandom = new SecureRandom();
	private final UserContextService userContextService;
	private final UserRepository userRepository;
	private final InviteCodeRepository inviteCodeRepository;

	@Transactional
	public UserUpdateResponse updateUserProfile(UserUpdateRequest userUpdateRequest) {
		User user = userContextService.getCurrentUser();
		user.initializeProfile(userUpdateRequest.nickname(), userUpdateRequest.imageObjectKey());
		return new UserUpdateResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
	}

	@Transactional
	public InvitationCodeResponse generateInvitationCode() {
		User user = userContextService.getCurrentUser();
		if (user.getCouple() != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		// inviteCode가 이미 존재하면 가져오고, 없으면 중복 확인해서 생성
		InviteCode inviteCode = inviteCodeRepository.findByUserAndStatus(user, CodeStatus.ACTIVE)
			.orElseGet(
				() -> {
					for (int i = 0; i < 20; i++) {
						String code = generate8DigitCode();
						if (!inviteCodeRepository.existsByCodeAndStatus(code, CodeStatus.ACTIVE)) {
							return inviteCodeRepository.save(
								InviteCode.builder().user(user).code(code).status(CodeStatus.ACTIVE).build());
						}
					}
					throw new NotFoundException(CODE_NOT_FOUND);
				}
			);
		return new InvitationCodeResponse(inviteCode.getCode());
	}

	private String generate8DigitCode() {
		int number = secureRandom.nextInt(90_000_000) + 10_000_000;
		return String.valueOf(number);
	}
}
