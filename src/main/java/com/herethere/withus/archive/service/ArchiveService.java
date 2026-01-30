package com.herethere.withus.archive.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.archive.dto.internal.ArchiveDayDto;
import com.herethere.withus.archive.dto.response.ArchiveDateResponse;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.repository.ArchiveRepository;
import com.herethere.withus.common.dto.internal.DateCursor;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.keyword.repository.KeywordRecordRepository;
import com.herethere.withus.question.domain.CoupleQuestion;
import com.herethere.withus.question.domain.QuestionPicture;
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
		DateCursor dateCursor = cursor == null ? null : cursorCodec.decode(cursor, DateCursor.class);
		LocalDate date = dateCursor == null ? null : dateCursor.date();
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		User partner = couple.getPartner(user.getId());
		List<ArchiveDayDto> images = archiveRepository.findArchiveDaysByCursor(couple.getId(),
			user.getId(), partner.getId(), date, size + 1);

		boolean hasNext = images.size() > size;

		String nextCursor = null;
		if (hasNext) {
			images = images.subList(0, size);
			LocalDate nextCursorDate = images.getLast().date();
			nextCursor = cursorCodec.encode(new DateCursor(nextCursorDate));
		}

		List<ArchiveListResponse.ArchiveInfo> archiveInfos = images.stream().map(
			i -> {
				String meImageUrl = i.meImageKey() == null
					? null : s3Service.createThumbnailImageUrl(i.meImageKey());

				String partnerImageUrl = i.partnerImageKey() == null
					? null : s3Service.createThumbnailImageUrl(i.partnerImageKey());

				return new ArchiveListResponse.ArchiveInfo(i.date(), meImageUrl, partnerImageUrl);
			}
		).toList();

		return new ArchiveListResponse(archiveInfos, hasNext, nextCursor);
	}

	@Transactional(readOnly = true)
	public ArchiveDateResponse getArchiveByDate(LocalDate date) {
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		User partner = couple.getPartner(user.getId());
		String myProfileImage = s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		String partnerProfileImage = s3Service.createThumbnailImageUrl(partner.getProfileImageKey());

		List<ArchiveDateResponse.ArchiveInfo> archiveInfoList = new ArrayList<>();

		// 질문
		CoupleQuestion coupleQuestion = coupleQuestionRepository.findByCoupleAndDate(couple, date).orElse(null);
		if (coupleQuestion != null) {
			String questionContent = coupleQuestion.getQuestion().getContent();
			QuestionPicture myQuestionPicture = archiveRepository.findByCoupleQuestionAndUser(
				coupleQuestion, user).orElse(null);
			QuestionPicture partnerQuestionPicture = archiveRepository.findByCoupleQuestionAndUser(
				coupleQuestion, partner).orElse(null);
			String myQuestionPictureImageUrl =
				myQuestionPicture == null ? null : s3Service.createOriginImageUrl(myQuestionPicture.getImageKey());
			String partnerQuestionPictureImageUrl = partnerQuestionPicture == null ? null :
				s3Service.createOriginImageUrl(partnerQuestionPicture.getImageKey());
			ArchiveDateResponse.ImageInfo myImageInfo = new ArchiveDateResponse.ImageInfo(user.getId(),
				user.getNickname(), myProfileImage, myQuestionPictureImageUrl,
				myQuestionPicture != null ? myQuestionPicture.getCreatedAt() : null);
			ArchiveDateResponse.ImageInfo partnerImageInfo = new ArchiveDateResponse.ImageInfo(partner.getId(),
				partner.getNickname(), partnerProfileImage, partnerQuestionPictureImageUrl,
				partnerQuestionPicture != null ? partnerQuestionPicture.getCreatedAt() : null);
			ArchiveDateResponse.ArchiveInfo archiveInfo = new ArchiveDateResponse.ArchiveInfo(questionContent,
				myImageInfo, partnerImageInfo);
			archiveInfoList.add(archiveInfo);
		}

		// 키워드
		List<KeywordRecord> keywordRecords = archiveRepository.findKeywordRecordsByCoupleAndDate(couple.getId(), date);

		Map<Long, List<KeywordRecord>> recordsByCoupleKeyword =
			keywordRecords.stream()
				.collect(Collectors.groupingBy(
					kr -> kr.getCoupleKeyword().getId()
				));

		for (List<KeywordRecord> records : recordsByCoupleKeyword.values()) {

			// 같은 coupleKeyword에 대한 기록들이므로 하나만 꺼내도 됨
			CoupleKeyword coupleKeyword = records.get(0).getCoupleKeyword();
			String keywordContent = coupleKeyword.getKeyword().getContent();

			KeywordRecord myRecord = records.stream()
				.filter(r -> r.getUser().getId().equals(user.getId()))
				.findFirst()
				.orElse(null);

			KeywordRecord partnerRecord = records.stream()
				.filter(r -> r.getUser().getId().equals(partner.getId()))
				.findFirst()
				.orElse(null);

			ArchiveDateResponse.ImageInfo myImageInfo = null;
			if (myRecord != null) {
				myImageInfo = new ArchiveDateResponse.ImageInfo(
					user.getId(),
					user.getNickname(),
					myProfileImage,
					s3Service.createOriginImageUrl(myRecord.getImageKey()),
					myRecord.getCreatedAt()
				);
			}

			ArchiveDateResponse.ImageInfo partnerImageInfo = null;
			if (partnerRecord != null) {
				partnerImageInfo = new ArchiveDateResponse.ImageInfo(
					partner.getId(),
					partner.getNickname(),
					partnerProfileImage,
					s3Service.createOriginImageUrl(partnerRecord.getImageKey()),
					partnerRecord.getCreatedAt()
				);
			}

			ArchiveDateResponse.ArchiveInfo archiveInfo =
				new ArchiveDateResponse.ArchiveInfo(
					keywordContent,
					myImageInfo,
					partnerImageInfo
				);
			archiveInfoList.add(archiveInfo);
		}
		return new ArchiveDateResponse(date, archiveInfoList);
	}
}

