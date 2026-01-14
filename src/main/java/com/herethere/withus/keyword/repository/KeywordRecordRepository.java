package com.herethere.withus.keyword.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.user.domain.User;

public interface KeywordRecordRepository extends JpaRepository<KeywordRecord, Long> {
	Optional<KeywordRecord> findByUserAndCoupleKeywordAndDate(User user, CoupleKeyword coupleKeyword, LocalDate date);

	boolean existsByUserAndCoupleKeywordAndDate(User user, CoupleKeyword coupleKeyword, LocalDate date);
}
