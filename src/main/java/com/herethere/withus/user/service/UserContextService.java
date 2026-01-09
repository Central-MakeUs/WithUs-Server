package com.herethere.withus.user.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Component;

import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserContextService {

	private final UserRepository userRepository;

	public User getCurrentUser() {
		Long userId = SecurityUtil.getCurrentUserId();
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}
}
