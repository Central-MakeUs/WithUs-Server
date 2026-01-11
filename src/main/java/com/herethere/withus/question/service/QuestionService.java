package com.herethere.withus.question.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.question.repository.QuestionRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final CoupleQuestionRepository coupleQuestionRepository;
	private final S3Service s3Service;
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

	@Transactional(readOnly = true)
	public TodayQuestionResponse getTodayQuestion() {
		User me = userContextService.getCurrentUser();
		Couple couple = me.getCouple();
		User partner = couple.getPartner(me.getId());

		Optional<CoupleQuestion> optionalCoupleQuestion = coupleQuestionRepository.findTopByCoupleOrderByCreatedAtDesc(
			couple);
		if (optionalCoupleQuestion.isEmpty()) {
			return new TodayQuestionResponse("오늘의 랜덤 질문이 조금 후에 도착해요!", null, null);
		} // Todo : question 시간 포함해서 수정하기

		CoupleQuestion coupleQuestion = optionalCoupleQuestion.get();

		QuestionPicture myPicture = questionPictureRepository.findByUserAndCoupleQuestion(me, coupleQuestion)
			.orElse(null);
		QuestionPicture partnerPicture = questionPictureRepository.findByUserAndCoupleQuestion(partner, coupleQuestion)
			.orElse(null);

		TodayQuestionResponse.MemberInfo myInfo = getMemberInfo(me, myPicture);
		TodayQuestionResponse.MemberInfo partnerInfo = getMemberInfo(partner, partnerPicture);

		return new TodayQuestionResponse(coupleQuestion.getQuestion().getContent(), myInfo, partnerInfo);
	}

	private TodayQuestionResponse.MemberInfo getMemberInfo(User user, QuestionPicture questionPicture) {
		String profileImageUrl = null;
		String questionImageUrl = null;
		LocalDateTime answeredAt = null;
		if (user.getProfileImageKey() != null) {
			profileImageUrl = s3Service.createGetPresignedUrl(user.getProfileImageKey());
		}
		if (questionPicture != null) {
			questionImageUrl = s3Service.createGetPresignedUrl(questionPicture.getImageKey());
			answeredAt = questionPicture.getCreatedAt();
		}

		return TodayQuestionResponse.MemberInfo.builder()
			.name(user.getNickname())
			.profileImageUrl(profileImageUrl)
			.questionImageUrl(questionImageUrl)
			.answeredAt(answeredAt)
			.build();
	}
}
