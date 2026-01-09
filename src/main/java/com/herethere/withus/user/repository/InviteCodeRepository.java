package com.herethere.withus.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.CodeStatus;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
	Optional<InviteCode> findByUserAndStatus(User user, CodeStatus status);
	boolean existsByUserAndStatus(User user, CodeStatus status);
	boolean existsByCodeAndStatus(String code, CodeStatus status);
}
