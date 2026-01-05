package com.herethere.withus.couple.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;

public interface CoupleRepository extends JpaRepository<Couple, Long> {
}
