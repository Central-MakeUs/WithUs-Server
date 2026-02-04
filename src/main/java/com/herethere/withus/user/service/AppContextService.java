package com.herethere.withus.user.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserStatus;
import com.herethere.withus.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppContextService {

	private final UserRepository userRepository;
	private final CoupleRepository coupleRepository;

	public User getCurrentUser() {
		Long userId = SecurityUtil.getCurrentUserId();
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}

	public User getActiveUser() {
		User user = getCurrentUser();
		if (user.getUserStatus() != UserStatus.ACTIVE) {
			throw new ForbiddenException(USER_DELETED);
		}
		return user;
	}

	public User getInitializedAndActiveUser() {
		User user = getCurrentUser();
		if (user.getUserStatus() != UserStatus.ACTIVE) {
			throw new ForbiddenException(USER_DELETED);
		}

		if (!user.isInitialized()) {
			throw new ConflictException(USER_NOT_INITIALIZED);
		}
		return user;
	}

	@Transactional(readOnly = true)
	public Couple getActiveCoupleRequired(User user) {
		return coupleRepository.findActiveCouple(user).orElseThrow(
			() -> new NotFoundException(COUPLE_NOT_FOUND)
		);
	}

	@Transactional(readOnly = true)
	public Optional<Couple> findActiveCouple(User user) {
		return coupleRepository.findActiveCouple(user);
	}
}
