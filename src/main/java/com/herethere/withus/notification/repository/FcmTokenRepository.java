package com.herethere.withus.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.notification.domain.FcmToken;
import com.herethere.withus.user.domain.User;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
	Optional<FcmToken> findByToken(String token);

	List<FcmToken> findAllByUser(User user);

	List<FcmToken> token(String token);

	void deleteByToken(String token);
}
