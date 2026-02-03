package com.herethere.withus.question.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.notification.dto.internal.FcmNotificationEvent;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.Question;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.dto.request.TodayQuestionImageRequest;
import com.herethere.withus.question.dto.response.TodayQuestionResponse;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.question.repository.QuestionRepository;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

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
	private final AppContextService appContextService;
	private final ApplicationEventPublisher eventPublisher;

	private Map<Long, Question> cachedQuestions;

	@PostConstruct
	public void init() {
		cachedQuestions = questionRepository.findAll()
			.stream()
			.collect(Collectors.toMap(
				Question::getQuestionNumber,
				q -> q
			));
	}

	public Map<Long, Question> getAllQuestionMap() {
		return cachedQuestions;
	}

	@Transactional
	public void uploadTodayQuestionImage(Long coupleQuestionId, TodayQuestionImageRequest request) {
		User user = appContextService.getInitializedUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		CoupleQuestion coupleQuestion = coupleQuestionRepository.findById(coupleQuestionId)
			.orElseThrow(() -> new NotFoundException(COUPLE_QUESTION_NOT_FOUND));

		if (!coupleQuestion.getCouple().getId().equals(couple.getId())) {
			throw new ConflictException(ACCESS_DENIED);
		}

		CoupleQuestion latestCoupleQuestion = coupleQuestionRepository.findTopByCoupleOrderByCreatedAtDesc(couple)
			.orElseThrow(() -> new NotFoundException(COUPLE_QUESTION_NOT_FOUND));

		if (!latestCoupleQuestion.getId().equals(coupleQuestion.getId())) {
			throw new BadRequestException(NOT_TODAY_QUESTION);
		}

		if (questionPictureRepository.existsByUserAndCoupleQuestion(user, coupleQuestion)) {
			throw new ConflictException(PICTURE_ALREADY_UPLOADED);
		}

		String finalImageKey = s3Service.processImagePublish(request.imageKey(), user.getId(), ImageType.ARCHIVE);

		QuestionPicture questionPicture = QuestionPicture.builder()
			.user(user)
			.coupleQuestion(coupleQuestion)
			.imageKey(finalImageKey)
			.build();

		questionPictureRepository.save(questionPicture);

		eventPublisher.publishEvent(FcmNotificationEvent.createUploadEvent(user, couple.getPartner(user.getId())));
	}

	@Transactional(readOnly = true)
	public TodayQuestionResponse getTodayQuestion() {
		User me = appContextService.getInitializedUser();
		Couple couple = appContextService.getActiveCoupleRequired(me);
		User partner = couple.getPartner(me.getId());

		Optional<CoupleQuestion> latestCoupleQuestion = coupleQuestionRepository.findTopByCoupleOrderByCreatedAtDesc(
			couple);

		// 만약 처음이라 CoupleQuestion이 없으면 대기 문구 반환
		if (latestCoupleQuestion.isEmpty()) {
			return new TodayQuestionResponse(null, generateWaitingResponse(), null, null);
		}

		CoupleQuestion coupleQuestion = latestCoupleQuestion.get();

		QuestionPicture myPicture = questionPictureRepository.findByUserAndCoupleQuestion(me, coupleQuestion)
			.orElse(null);
		QuestionPicture partnerPicture = questionPictureRepository.findByUserAndCoupleQuestion(partner, coupleQuestion)
			.orElse(null);

		TodayQuestionResponse.ImageInfo myInfo = getImageInfo(me, myPicture);
		TodayQuestionResponse.ImageInfo partnerInfo = getImageInfo(partner, partnerPicture);

		return new TodayQuestionResponse(coupleQuestion.getId(), coupleQuestion.getQuestion().getContent(), myInfo,
			partnerInfo);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processCoupleQuestions(Couple couple, Map<Long, Question> questionMap, LocalDate date) {
		// couple 영속성 컨텍스트 관리
		couple = coupleRepository.findByIdWithLock(couple.getId())
			.orElseThrow(() -> new NotFoundException(COUPLE_NOT_FOUND));

		long questionNumber = couple.updateToNextQuestion(date);
		Question question = questionMap.get(questionNumber);
		if (question == null) {
			throw new NotFoundException(QUESTION_NOT_FOUND);
		}

		CoupleQuestion coupleQuestion = CoupleQuestion.builder().couple(couple).question(question).date(date).build();

		coupleQuestionRepository.save(coupleQuestion);

		eventPublisher.publishEvent(FcmNotificationEvent.createNewQuestionEvent(couple.getUserA()));
		eventPublisher.publishEvent(FcmNotificationEvent.createNewQuestionEvent(couple.getUserB()));
	}

	private TodayQuestionResponse.ImageInfo getImageInfo(User user, QuestionPicture questionPicture) {
		String profileImageUrl = null;
		String questionImageUrl = null;
		LocalDateTime answeredAt = null;
		if (user.getProfileImageKey() != null) {
			profileImageUrl = s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		}
		if (questionPicture != null) {
			questionImageUrl = s3Service.createOriginImageUrl(questionPicture.getImageKey());
			answeredAt = questionPicture.getCreatedAt();
		}

		return TodayQuestionResponse.ImageInfo.builder()
			.userId(user.getId())
			.name(user.getNickname())
			.profileThumbnailImageUrl(profileImageUrl)
			.questionImageUrl(questionImageUrl)
			.answeredAt(answeredAt)
			.build();
	}

	private String generateWaitingResponse() {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		ZonedDateTime target = now.toLocalDate().atStartOfDay(now.getZone());

		if (now.isAfter(target)) {
			target = target.plusDays(1);
		}

		Duration duration = Duration.between(now, target);

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
