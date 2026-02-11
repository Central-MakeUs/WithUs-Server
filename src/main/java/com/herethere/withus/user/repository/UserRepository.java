package com.herethere.withus.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProviderAndProviderIdAndUserStatus(OAuthProviderType provider, String providerId,
		UserStatus status);
}
