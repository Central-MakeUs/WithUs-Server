package com.herethere.withus.common.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ErrorCode;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleStatus;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class CoupleStatusAspect {

	private final UserContextService userContextService;

	@Before("@annotation(com.herethere.withus.common.annotation.RequiresActiveCouple)")
	public void validateCoupleActive() {
		Couple couple = userContextService.getCurrentUser().getCouple();

		if (couple == null || couple.getStatus() != CoupleStatus.ACTIVE) {
			throw new ConflictException(ErrorCode.COUPLE_NOT_ACTIVE);
		}
	}
}
