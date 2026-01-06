package com.herethere.withus.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.auth.domain.OAuthProviderType;
import com.herethere.withus.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByProviderAndProviderId(OAuthProviderType provider, Long providerId);
}
