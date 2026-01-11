package com.herethere.withus.question.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.question.repository.QuestionRepository;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final CoupleQuestionRepository coupleQuestionRepository;
	private final UserContextService userContextService;

	@Transactional
	public void uploadTodayQuestionImage(TodayQuestionImageRequest request) {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();
		CoupleQuestion coupleQuestion = coupleQuestionRepository.findTopByCoupleOrderByCreatedAtDesc(couple)
			.orElseThrow(() -> new NotFoundException(COUPLE_QUESTION_NOT_FOUND));

		if (questionPictureRepository.existsByUserAndCoupleQuestion(user, coupleQuestion)) {
			throw new ConflictException(PICTURE_ALREADY_UPLOADED);
		}

		QuestionPicture questionPicture = QuestionPicture.builder()
			.user(user)
			.coupleQuestion(coupleQuestion)
			.imageKey(request.imageKey())
			.build();

		questionPictureRepository.save(questionPicture);
	}
}
