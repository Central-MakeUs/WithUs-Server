package com.herethere.withus.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.domain.UserKeyword;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
	List<UserKeyword> findAllByUserIn(List<User> userList);
}
