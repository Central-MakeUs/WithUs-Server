package com.herethere.withus.couple.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleManager {
	private final CoupleRepository coupleRepository;

	@Transactional(readOnly = true)
	public Couple getCouple(User user) {
		return coupleRepository.findActiveCouple(user).orElseThrow(
			() -> new NotFoundException(COUPLE_NOT_FOUND)
		);
	}
}
