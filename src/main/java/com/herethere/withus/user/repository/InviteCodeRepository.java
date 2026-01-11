package com.herethere.withus.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
	Optional<InviteCode> findByUser(User user);
	boolean existsByCode(String code);
	Optional<InviteCode> findByCode(String code);
	void deleteByUser(User user);
	void delete(InviteCode inviteCode);
}
