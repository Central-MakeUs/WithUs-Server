package com.herethere.withus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.UserKeyword;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
}
