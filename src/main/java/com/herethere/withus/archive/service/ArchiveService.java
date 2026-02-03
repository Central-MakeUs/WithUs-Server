package com.herethere.withus.archive.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.archive.dto.internal.ArchiveDayView;
import com.herethere.withus.archive.dto.internal.ArchiveDetailView;
import com.herethere.withus.archive.dto.internal.DailyArchiveView;
import com.herethere.withus.archive.dto.response.ArchiveCalendarResponse;
import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionDetailResponse;
import com.herethere.withus.archive.dto.response.ArchiveQuestionListResponse;
import com.herethere.withus.archive.enums.ArchiveType;
import com.herethere.withus.archive.repository.ArchiveRepository;
import com.herethere.withus.common.dto.internal.DateCursor;
import com.herethere.withus.common.dto.internal.NumberCursor;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.Question;
import com.herethere.withus.question.domain.QuestionPicture;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArchiveService {

	private final ArchiveRepository archiveRepository;
	private final CoupleQuestionRepository coupleQuestionRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final S3Service s3Service;
	private final AppContextService appContextService;
	private final CursorCodec cursorCodec;

	@Transactional(readOnly = true)
	public ArchiveListResponse getArchivesByCursor(String cursor, int size) {
		DateCursor dateCursor = cursorCodec.decode(cursor, DateCursor.class);
		LocalDate lastDate = dateCursor == null ? null : dateCursor.date();

		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		User partner = couple.getPartner(user.getId());

		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		List<Date> dateResults = archiveRepository.findTargetDates(couple.getId(), lastDate, today, size + 1);
		List<LocalDate> targetDates = dateResults.stream()
			.map(Date::toLocalDate)
			.toList();

		boolean hasNext = targetDates.size() > size;

		targetDates = hasNext ? targetDates.subList(0, size) : targetDates;

		if (targetDates.isEmpty()) {
			return new ArchiveListResponse(List.of(), false, null);
		}

		LocalDate nextCursorDate = targetDates.getLast();
		String nextCursor = hasNext ? cursorCodec.encode(nextCursorDate) : null;

		List<ArchiveDayView> archiveDayViews = archiveRepository.findAllByDates(couple.getId(), user.getId(),
			partner.getId(), targetDates);

		Map<LocalDate, List<ArchiveListResponse.ImageInfo>> groupedByDate = archiveDayViews.stream()
			.collect(Collectors.groupingBy(
				ArchiveDayView::getDate,
				LinkedHashMap::new,
				Collectors.mapping(dto -> new ArchiveListResponse.ImageInfo(
					ArchiveType.from(dto.getArchiveType()),
					dto.getSourceId(),
					s3Service.createThumbnailImageUrl(dto.getMeImageKey()),
					s3Service.createThumbnailImageUrl(dto.getPartnerImageKey())
				), Collectors.toList())
			));

		List<ArchiveListResponse.ArchiveInfo> archiveList = groupedByDate.entrySet().stream()
			.map(entry -> new ArchiveListResponse.ArchiveInfo(entry.getKey(), entry.getValue()))
			.toList();

		return new ArchiveListResponse(archiveList, hasNext, nextCursor);
	}

	@Transactional(readOnly = true)
	public ArchiveDateResponse getArchiveByDate(LocalDate date, Long targetId, ArchiveType targetType) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		User partner = couple.getPartner(user.getId());
		String myProfileUrl =
			user.getProfileImageKey() == null ? null : s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		String partnerProfileUrl = partner.getProfileImageKey() == null ? null :
			s3Service.createThumbnailImageUrl(partner.getProfileImageKey());

		List<ArchiveDetailView> archiveDetailViews = archiveRepository.findDetailByDate(couple.getId(), user.getId(),
			partner.getId(), date);

		List<ArchiveDateResponse.ArchiveInfo> archiveInfos = archiveDetailViews.stream().map(v -> {
			String meArchiveImageUrl = s3Service.createOriginImageUrl(v.getMeImageKey());
			String partnerArchiveImageUrl = s3Service.createOriginImageUrl(v.getPartnerImageKey());
			ArchiveDateResponse.ImageInfo myInfo = new ArchiveDateResponse.ImageInfo(user.getId(), user.getNickname(),
				myProfileUrl, meArchiveImageUrl, v.getMeAnsweredAt());
			ArchiveDateResponse.ImageInfo partnerInfo = new ArchiveDateResponse.ImageInfo(partner.getId(),
				partner.getNickname(), partnerProfileUrl, partnerArchiveImageUrl, v.getPartnerAnsweredAt());
			boolean selected = Objects.equals(v.getSourceId(), targetId)
				&& Objects.equals(ArchiveType.valueOf(v.getArchiveType()), targetType);
			return new ArchiveDateResponse.ArchiveInfo(ArchiveType.from(v.getArchiveType()), v.getSourceId(),
				v.getContent(), myInfo, partnerInfo, selected);
		}).toList();

		return new ArchiveDateResponse(date, archiveInfos);
	}

	@Transactional(readOnly = true)
	public ArchiveQuestionListResponse getArchiveQuestions(int size, String cursor) {
		NumberCursor numberCursor = cursorCodec.decode(cursor, NumberCursor.class);
		Long number = numberCursor == null ? null : numberCursor.number();

		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);

		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		Pageable pageable = PageRequest.of(0, size);
		Slice<CoupleQuestion> result = coupleQuestionRepository.findNextQuestions(couple.getId(), number, today,
			pageable);

		boolean hasNext = result.hasNext();
		List<CoupleQuestion> coupleQuestions = result.getContent();

		String nextCursor = null;
		if (result.hasNext()) {
			long lastNumberOnPage = result.getContent().getLast().getQuestion().getQuestionNumber();
			nextCursor = cursorCodec.encode(new NumberCursor(lastNumberOnPage));
		}

		List<ArchiveQuestionListResponse.QuestionInfo> questionInfos = coupleQuestions.stream().map(cq -> {
			Question question = cq.getQuestion();
			return new ArchiveQuestionListResponse.QuestionInfo(cq.getId(), question.getQuestionNumber(),
				question.getContent());
		}).toList();

		return new ArchiveQuestionListResponse(questionInfos, hasNext, nextCursor);
	}

	@Transactional(readOnly = true)
	public ArchiveQuestionDetailResponse getDetailArchiveQuestion(Long coupleQuestionId) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		User partner = couple.getPartner(user.getId());

		CoupleQuestion coupleQuestion = coupleQuestionRepository.findById(coupleQuestionId).orElseThrow(
			() -> new NotFoundException(COUPLE_QUESTION_NOT_FOUND));

		if (!coupleQuestion.getCouple().getId().equals(couple.getId())) {
			throw new NotFoundException(COUPLE_QUESTION_NOT_FOUND);
		}

		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		if (!coupleQuestion.getDate().isBefore(today)) {
			throw new ForbiddenException(CANNOT_VIEW_TODAY_QUESTION);
		}

		String myProfileUrl = s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		String partnerProfileUrl = s3Service.createThumbnailImageUrl(partner.getProfileImageKey());

		QuestionPicture myQuestionPicture = questionPictureRepository.findByUserAndCoupleQuestion(user, coupleQuestion)
			.orElse(null);
		QuestionPicture partnerQuestionPicture = questionPictureRepository.findByUserAndCoupleQuestion(partner,
			coupleQuestion).orElse(null);

		ArchiveQuestionDetailResponse.ImageInfo myInfo = createImageInfo(user, myQuestionPicture);
		ArchiveQuestionDetailResponse.ImageInfo partnerInfo = createImageInfo(partner, partnerQuestionPicture);

		Question question = coupleQuestion.getQuestion();

		return new ArchiveQuestionDetailResponse(coupleQuestionId, question.getQuestionNumber(), question.getContent(),
			myInfo, partnerInfo);
	}

	@Transactional(readOnly = true)
	public ArchiveCalendarResponse getArchiveCalendar(int year, int month) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		User partner = couple.getPartner(user.getId());

		YearMonth yearMonth = YearMonth.of(year, month);
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate endDate = yearMonth.atEndOfMonth();
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		List<DailyArchiveView> dailyArchiveRows = archiveRepository.findDailyArchives(couple.getId(), user.getId(),
			partner.getId(), startDate, endDate, today);

		List<ArchiveCalendarResponse.ArchiveDay> archiveDays = dailyArchiveRows.stream().map(r ->
				new ArchiveCalendarResponse.ArchiveDay(
					r.getArchiveDate(),
					s3Service.createThumbnailImageUrl(r.getMeImageKey()),
					s3Service.createThumbnailImageUrl(r.getPartnerImageKey())))
			.toList();
		return new ArchiveCalendarResponse(year, month, archiveDays);
	}

	private ArchiveQuestionDetailResponse.ImageInfo createImageInfo(User user, QuestionPicture picture) {
		String profileUrl = s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		String imageUrl = (picture != null) ? s3Service.createOriginImageUrl(picture.getImageKey()) : null;
		LocalDateTime createdAt = (picture != null) ? picture.getCreatedAt() : null;

		return new ArchiveQuestionDetailResponse.ImageInfo(
			user.getId(),
			user.getNickname(),
			profileUrl,
			imageUrl,
			createdAt
		);
	}
}

