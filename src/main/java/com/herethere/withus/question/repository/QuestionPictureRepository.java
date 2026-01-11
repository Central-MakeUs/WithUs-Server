package com.herethere.withus.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.question.domain.QuestionPicture;

public interface QuestionPictureRepository extends JpaRepository<QuestionPicture, Long> {
}
