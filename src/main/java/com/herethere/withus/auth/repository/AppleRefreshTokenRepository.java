package com.herethere.withus.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.auth.domain.AppleRefreshToken;
import com.herethere.withus.user.domain.User;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, Long> {
	Optional<AppleRefreshToken> findByUser(User user);
}
