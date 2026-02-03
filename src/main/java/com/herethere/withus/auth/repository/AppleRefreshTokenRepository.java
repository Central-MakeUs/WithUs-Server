package com.herethere.withus.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.auth.domain.AppleRefreshToken;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, Long> {
}
