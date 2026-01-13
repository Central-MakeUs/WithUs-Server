package com.herethere.withus.question.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.annotation.RequiresActiveCouple;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.Question;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.question.repository.QuestionRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
	private final QuestionRepository questionRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final CoupleQuestionRepository coupleQuestionRepository;
	private final CoupleRepository coupleRepository;
	private final S3Service s3Service;
	private final UserContextService userContextService;

	private Map<Long, Question> cachedQuestions;

	@PostConstruct
	public void init() {
		cachedQuestions = questionRepository.findAll()
			.stream()
			.collect(Collectors.toMap(
				Question::getId,
				q -> q
			));
	}

	public Map<Long, Question> getAllQuestionMap() {
		return cachedQuestions;
	}

	@Transactional
	@RequiresActiveCouple
	public void uploadTodayQuestionImage(TodayQuestionImageRequest request) {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();
		CoupleQuestion coupleQuestion = coupleQuestionRepository.findById(request.coupleQuestionId())
			.orElseThrow(() -> new NotFoundException(COUPLE_QUESTION_NOT_FOUND));

		if (!coupleQuestion.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(ACCESS_DENIED);
		}

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
	@RequiresActiveCouple
	public TodayQuestionResponse getTodayQuestion() {
		User me = userContextService.getCurrentUser();
		Couple couple = me.getCouple();
		User partner = couple.getPartner(me.getId());

		Optional<CoupleQuestion> optionalCoupleQuestion = coupleQuestionRepository.findTopByCoupleOrderByCreatedAtDesc(
			couple);

		// 만약 처음이라 CoupleQuestion이 없으면 대기 문구 반환
		if (optionalCoupleQuestion.isEmpty()) {
			return new TodayQuestionResponse(null, generateWaitingResponse(couple), null, null);
		}

		CoupleQuestion coupleQuestion = optionalCoupleQuestion.get();

		QuestionPicture myPicture = questionPictureRepository.findByUserAndCoupleQuestion(me, coupleQuestion)
			.orElse(null);
		QuestionPicture partnerPicture = questionPictureRepository.findByUserAndCoupleQuestion(partner, coupleQuestion)
			.orElse(null);

		TodayQuestionResponse.MemberInfo myInfo = getMemberInfo(me, myPicture);
		TodayQuestionResponse.MemberInfo partnerInfo = getMemberInfo(partner, partnerPicture);

		return new TodayQuestionResponse(coupleQuestion.getId(), coupleQuestion.getQuestion().getContent(), myInfo,
			partnerInfo);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processCoupleQuestions(Couple couple, Map<Long, Question> questionMap, LocalDate date) {
		// couple 영속성 컨텍스트 관리
		couple = coupleRepository.findByIdWithLock(couple.getId())
			.orElseThrow(() -> new NotFoundException(COUPLE_NOT_FOUND));

		long questionId = couple.updateToNextQuestion(date);
		Question question = questionMap.get(questionId);
		if (question == null) {
			throw new NotFoundException(QUESTION_NOT_FOUND);
		}

		CoupleQuestion coupleQuestion = CoupleQuestion.builder().couple(couple).question(question).date(date).build();

		coupleQuestionRepository.save(coupleQuestion);
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

	private String generateWaitingResponse(Couple couple) {
		LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
		LocalTime target = couple.getQuestionTime();
		Duration duration = Duration.between(now, target);

		if (duration.isNegative()) {
			duration = duration.plusDays(1);
		}

		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();

		if (hours == 0 && minutes == 0) {
			return "오늘의 랜덤 질문이 잠시 후 도착해요!";
		}

		StringBuilder sb = new StringBuilder("오늘의 랜덤 질문이 ");

		if (hours > 0) {
			sb.append(hours).append("시간 ");
		}

		if (minutes > 0) {
			sb.append(minutes).append("분 ");
		}

		sb.append("후에 도착해요!");

		return sb.toString();
	}
}
