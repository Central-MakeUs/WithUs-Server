package com.herethere.withus.couple.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.OnboardingStatus;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnboardingManager {
	private final AppContextService appContextService;

	public OnboardingStatus getStatus(User user) {
		if (!user.isInitialized()) {
			return OnboardingStatus.NEED_USER_INITIAL_SETUP;
		}

		Optional<Couple> couple = appContextService.findActiveCouple(user);
		if (couple.isEmpty()) {
			return OnboardingStatus.NEED_COUPLE_CONNECT;
		}

		return OnboardingStatus.COMPLETED;
	}
}
