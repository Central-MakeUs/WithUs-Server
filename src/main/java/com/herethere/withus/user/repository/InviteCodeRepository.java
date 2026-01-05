package com.herethere.withus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.InviteCode;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
}
