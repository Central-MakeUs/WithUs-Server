package com.herethere.withus.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
