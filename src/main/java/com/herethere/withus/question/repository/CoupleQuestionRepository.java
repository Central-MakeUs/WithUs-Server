package com.herethere.withus.question.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.question.domain.CoupleQuestion;

public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestion, Long> {
	// 가장 최신의 CoupleQuestion 가져오기
	Optional<CoupleQuestion> findTopByCoupleOrderByCreatedAtDesc(Couple couple);
}
