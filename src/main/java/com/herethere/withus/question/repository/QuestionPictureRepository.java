package com.herethere.withus.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.user.domain.User;

public interface QuestionPictureRepository extends JpaRepository<QuestionPicture, Long> {
	boolean existsByUserAndCoupleQuestion(User user, CoupleQuestion coupleQuestion);
}
