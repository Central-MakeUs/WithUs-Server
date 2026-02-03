package com.herethere.withus.keyword.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ErrorCode;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.domain.CoupleKeywordStatus;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.keyword.dto.request.TodayKeywordImageRequest;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.dto.response.TodayKeywordResponse;
import com.herethere.withus.keyword.repository.KeywordRecordRepository;
import com.herethere.withus.keyword.repository.KeywordRepository;
import com.herethere.withus.notification.dto.internal.FcmNotificationEvent;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordService {
	private final KeywordRepository keywordRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;
	private final KeywordRecordRepository keywordRecordRepository;
	private final S3Service s3Service;
	private final AppContextService appContextService;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional(readOnly = true)
	public DefaultKeywordsResponse getDefaultKeywords() {
		List<Keyword> keywordList = keywordRepository.findAllByIsDefaultTrueOrderByDisplayOrderAsc();
		List<DefaultKeywordsResponse.KeywordInfo> keywordInfos = keywordList.stream()
			.map(k -> new DefaultKeywordsResponse.KeywordInfo(k.getId(), k.getContent(), k.getDisplayOrder()))
			.sorted(Comparator.comparing(DefaultKeywordsResponse.KeywordInfo::displayOrder))
			.toList();
		return new DefaultKeywordsResponse(keywordInfos);
	}

	@Transactional(readOnly = true)
	public CoupleKeywordsResponse getCoupleKeywords() {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(user);

		List<CoupleKeyword> coupleKeywords = coupleKeywordRepository.findAllByCoupleAndStatus(couple,
			CoupleKeywordStatus.ACTIVE);
		List<CoupleKeywordsResponse.CoupleKeywordInfo> coupleKeywordInfos = coupleKeywords.stream().map(c -> {
				Keyword keyword = c.getKeyword();
				return new CoupleKeywordsResponse.CoupleKeywordInfo(keyword.getId(), c.getId(), keyword.getContent());
			}).sorted(Comparator.comparing(CoupleKeywordsResponse.CoupleKeywordInfo::content))
			.toList();

		return new CoupleKeywordsResponse(coupleKeywordInfos);
	}

	@Transactional(readOnly = true)
	public TodayKeywordResponse getTodayCoupleKeyword(Long coupleKeywordId) {
		User me = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(me);
		User partner = couple.getPartner(me.getId());
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		CoupleKeyword coupleKeyword = coupleKeywordRepository.findById(coupleKeywordId).orElseThrow(
			() -> new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND)
		);
		if (coupleKeyword.getStatus() == CoupleKeywordStatus.DELETED) {
			throw new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND);
		}

		Keyword keyword = coupleKeyword.getKeyword();

		if (!coupleKeyword.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}

		KeywordRecord myRecord = keywordRecordRepository.findByUserAndCoupleKeywordAndDate(me, coupleKeyword, today)
			.orElse(null);
		KeywordRecord partnerRecord = keywordRecordRepository.findByUserAndCoupleKeywordAndDate(partner, coupleKeyword,
			today).orElse(null);

		TodayKeywordResponse.ImageInfo myInfo = getImageInfo(me, myRecord);
		TodayKeywordResponse.ImageInfo partnerInfo = getImageInfo(partner, partnerRecord);

		return new TodayKeywordResponse(coupleKeyword.getId(), generateKeywordQuestion(keyword), myInfo, partnerInfo);
	}

	@Transactional
	public void uploadTodayCoupleKeywordPicture(Long coupleKeywordId, TodayKeywordImageRequest request) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(user);
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		CoupleKeyword coupleKeyword = coupleKeywordRepository.findById((coupleKeywordId))
			.orElseThrow(() -> new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND));

		if (coupleKeyword.getStatus() == CoupleKeywordStatus.DELETED) {
			throw new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND);
		}

		if (!coupleKeyword.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}

		if (keywordRecordRepository.existsByUserAndCoupleKeywordAndDate(user, coupleKeyword, today)) {
			throw new ConflictException(ErrorCode.PICTURE_ALREADY_UPLOADED);
		}

		String finalImageKey = s3Service.processImagePublish(request.imageKey(), user.getId(), ImageType.ARCHIVE);

		KeywordRecord keywordRecord = KeywordRecord.builder()
			.coupleKeyword(coupleKeyword)
			.user(user)
			.date(today)
			.imageKey(finalImageKey)
			.build();
		keywordRecordRepository.save(keywordRecord);

		eventPublisher.publishEvent(FcmNotificationEvent.createUploadEvent(user, couple.getPartner(user.getId())));
	}

	public Set<Keyword> getChosenKeywords(List<Long> defaultKeywordIds, List<String> customKeywords) {
		Set<Keyword> chosenKeywords = new LinkedHashSet<>();
		chosenKeywords.addAll(keywordRepository.findAllById(defaultKeywordIds));

		// 커스텀 키워드 저장
		for (String keywordContent : customKeywords) {
			String trimmedKeyword = keywordContent.trim();
			if (trimmedKeyword.isEmpty()) {
				continue;
			}
			Keyword keyword = keywordRepository.findByContent(trimmedKeyword).orElseGet(
				() -> {
					return keywordRepository.save(
						Keyword.builder()
							.content(trimmedKeyword)
							.isDefault(false)
							.build()
					);
				}
			);
			chosenKeywords.add(keyword);
		}
		return chosenKeywords;
	}

	private TodayKeywordResponse.ImageInfo getImageInfo(User user, KeywordRecord keywordRecord) {
		String profileImageUrl = null;
		String questionImageUrl = null;
		LocalDateTime answeredAt = null;
		if (user.getProfileImageKey() != null) {
			profileImageUrl = s3Service.createThumbnailImageUrl(user.getProfileImageKey());
		}
		if (keywordRecord != null) {
			questionImageUrl = s3Service.createOriginImageUrl(keywordRecord.getImageKey());
			answeredAt = keywordRecord.getCreatedAt();
		}

		return TodayKeywordResponse.ImageInfo.builder()
			.userId(user.getId())
			.name(user.getNickname())
			.profileThumbnailImageUrl(profileImageUrl)
			.questionImageUrl(questionImageUrl)
			.answeredAt(answeredAt)
			.build();
	}

	private String generateKeywordQuestion(Keyword keyword) {
		String content = keyword.getContent();
		return "오늘의 \"" + content + "\" 사진은?";
	}
}
