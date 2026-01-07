package com.herethere.withus.user.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.security.SecurityUtil;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.dto.request.UserUpdateRequest;
import com.herethere.withus.user.dto.response.UserUpdateResponse;
import com.herethere.withus.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public UserUpdateResponse updateUserProfile(UserUpdateRequest userUpdateRequest) {
		User user = getCurrentUser();
		user.initializeProfile(userUpdateRequest.nickname(), userUpdateRequest.imageObjectKey());
		return new UserUpdateResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
	}

	private User getCurrentUser() {
		Long userId = SecurityUtil.getCurrentUserId();
		return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
	}
}
