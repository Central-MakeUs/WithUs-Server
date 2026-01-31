package com.herethere.withus.question.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.question.domain.CoupleQuestion;

public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestion, Long> {
	// 가장 최신의 CoupleQuestion 가져오기
	Optional<CoupleQuestion> findTopByCoupleOrderByCreatedAtDesc(Couple couple);

	Optional<CoupleQuestion> findById(Long id);

	Optional<CoupleQuestion> findByCoupleAndDate(Couple couple, LocalDate date);

	@Query("""
		SELECT cq FROM CoupleQuestion cq
		JOIN FETCH cq.question q
		WHERE cq.couple.id = :coupleId
		AND (:lastNumber IS NULL OR q.questionNumber > :lastNumber)
		ORDER BY q.questionNumber ASC
		""")
	Slice<CoupleQuestion> findNextQuestions(
		@Param("coupleId") Long coupleId,
		@Param("lastNumber") Long lastNumber,
		Pageable pageable
	);
}
