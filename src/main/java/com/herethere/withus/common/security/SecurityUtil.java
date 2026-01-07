package com.herethere.withus.common.security;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.herethere.withus.common.exception.UnauthenticatedUserException;

public class SecurityUtil {
	public static Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new UnauthenticatedUserException(UNAUTHENTICATED_USER);
		}

		return (Long) authentication.getPrincipal();
	}
}
