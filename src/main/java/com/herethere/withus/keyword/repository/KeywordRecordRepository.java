package com.herethere.withus.keyword.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.user.domain.User;

public interface KeywordRecordRepository extends JpaRepository<KeywordRecord, Long> {
	Optional<KeywordRecord> findByUserAndCoupleKeywordAndDate(User user, CoupleKeyword coupleKeyword, LocalDate date);

	boolean existsByUserAndCoupleKeywordAndDate(User user, CoupleKeyword coupleKeyword, LocalDate date);

	@Query("""
		SELECT k FROM KeywordRecord k
		JOIN FETCH k.coupleKeyword ck
		WHERE ck.couple.id = :coupleId
		AND k.date >= :start AND k.date <= :end
		""")
	List<KeywordRecord> findAllByCoupleInPeriod(
		@Param("coupleId") Long coupleId,
		@Param("start") LocalDate start,
		@Param("end") LocalDate end
	);
}
