package com.herethere.withus.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.question.domain.CoupleQuestion;

public interface CoupleQuestionRepository extends JpaRepository<CoupleQuestion, Long> {
}
