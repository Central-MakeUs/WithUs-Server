package com.herethere.withus.couple;

import org.springframework.stereotype.Service;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleStatus;
import com.herethere.withus.couple.domain.OnboardingStatus;
import com.herethere.withus.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnboardingManager {
	public OnboardingStatus getStatus(User user) {
		if (!user.isInitialized()) {
			return OnboardingStatus.NEED_USER_INITIAL_SETUP;
		}

		Couple couple = user.getCouple();

		if (couple == null) {
			return OnboardingStatus.NEED_COUPLE_CONNECT;
		}
		if (couple.getStatus() != CoupleStatus.ACTIVE) {
			return OnboardingStatus.NEED_COUPLE_INITIAL_SETUP;
		}

		return OnboardingStatus.COMPLETED;
	}
}
