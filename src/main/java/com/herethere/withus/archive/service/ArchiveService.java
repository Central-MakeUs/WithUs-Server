package com.herethere.withus.archive.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.archive.dto.internal.ArchiveDayView;
import com.herethere.withus.archive.dto.internal.ArchiveDetailView;
import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.enums.ArchiveType;
import com.herethere.withus.archive.repository.ArchiveRepository;
import com.herethere.withus.common.dto.internal.DateCursor;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.keyword.repository.KeywordRecordRepository;
import com.herethere.withus.question.repository.CoupleQuestionRepository;
import com.herethere.withus.question.repository.QuestionPictureRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArchiveService {

	private final UserContextService userContextService;
	private final ArchiveRepository archiveRepository;
	private final CoupleQuestionRepository coupleQuestionRepository;
	private final QuestionPictureRepository questionPictureRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;
	private final KeywordRecordRepository keywordRecordRepository;
	private final S3Service s3Service;
	private final CursorCodec cursorCodec;

	@Transactional(readOnly = true)
	public ArchiveListResponse getArchivesByCursor(String cursor, int size) {
		DateCursor dateCursor = cursorCodec.decode(cursor, DateCursor.class);
		LocalDate lastDate = dateCursor == null ? null : dateCursor.date();

		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		User partner = couple.getPartner(user.getId());

		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		List<LocalDate> targetDates = archiveRepository.findTargetDates(couple.getId(), lastDate, today, size + 1);

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
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
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
}

