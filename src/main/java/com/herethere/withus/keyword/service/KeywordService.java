package com.herethere.withus.keyword.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.annotation.RequiresActiveCouple;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.ErrorCode;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.domain.KeywordRecord;
import com.herethere.withus.keyword.dto.request.TodayKeywordImageRequest;
import com.herethere.withus.keyword.dto.response.CoupleKeywordsResponse;
import com.herethere.withus.keyword.dto.response.DefaultKeywordsResponse;
import com.herethere.withus.keyword.dto.response.TodayKeywordResponse;
import com.herethere.withus.keyword.repository.KeywordRecordRepository;
import com.herethere.withus.keyword.repository.KeywordRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordService {
	private final KeywordRepository keywordRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;
	private final KeywordRecordRepository keywordRecordRepository;
	private final S3Service s3Service;
	private final UserContextService userContextService;

	@Transactional(readOnly = true)
	public DefaultKeywordsResponse getDefaultKeywords() {
		List<Keyword> keywordList = keywordRepository.findAllByIsDefaultTrueOrderByDisplayOrderAsc();
		List<DefaultKeywordsResponse.KeywordInfo> keywordInfos = keywordList.stream()
			.map(k -> new DefaultKeywordsResponse.KeywordInfo(k.getId(), k.getContent(), k.getDisplayOrder()))
			.toList();
		return new DefaultKeywordsResponse(keywordInfos);
	}

	@Transactional(readOnly = true)
	@RequiresActiveCouple
	public CoupleKeywordsResponse getCoupleKeywords() {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();

		List<CoupleKeyword> coupleKeywords = coupleKeywordRepository.findAllByCouple(couple);
		List<CoupleKeywordsResponse.CoupleKeywordInfo> coupleKeywordInfos = coupleKeywords.stream().map(c -> {
			Keyword keyword = c.getKeyword();
			return new CoupleKeywordsResponse.CoupleKeywordInfo(keyword.getId(), c.getId(), keyword.getContent());
		}).toList();

		return new CoupleKeywordsResponse(coupleKeywordInfos);
	}

	@Transactional(readOnly = true)
	@RequiresActiveCouple
	public TodayKeywordResponse getTodayCoupleKeyword(Long coupleKeywordId) {
		User me = userContextService.getCurrentUser();
		Couple couple = me.getCouple();
		User partner = couple.getPartner(me.getId());
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		CoupleKeyword coupleKeyword = coupleKeywordRepository.findById((coupleKeywordId)).orElseThrow(
			() -> new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND)
		);
		Keyword keyword = coupleKeyword.getKeyword();

		if (!coupleKeyword.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}

		KeywordRecord myRecord = keywordRecordRepository.findByUserAndCoupleKeywordAndDate(me, coupleKeyword, today)
			.orElse(null);
		KeywordRecord partnerRecord = keywordRecordRepository.findByUserAndCoupleKeywordAndDate(partner, coupleKeyword,
			today).orElse(null);

		TodayKeywordResponse.MemberInfo myInfo = getMemberInfo(me, myRecord);
		TodayKeywordResponse.MemberInfo partnerInfo = getMemberInfo(partner, partnerRecord);

		return new TodayKeywordResponse(coupleKeyword.getId(), generateKeywordQuestion(keyword), myInfo, partnerInfo);
	}

	@Transactional
	@RequiresActiveCouple
	public void uploadTodayCoupleKeywordPicture(Long coupleKeywordId, TodayKeywordImageRequest request) {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

		CoupleKeyword coupleKeyword = coupleKeywordRepository.findById((coupleKeywordId))
			.orElseThrow(() -> new NotFoundException(ErrorCode.COUPLE_KEYWORD_NOT_FOUND));

		if (!coupleKeyword.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(ErrorCode.ACCESS_DENIED);
		}

		if (keywordRecordRepository.existsByUserAndCoupleKeywordAndDate(user, coupleKeyword, today)) {
			throw new ConflictException(ErrorCode.PICTURE_ALREADY_UPLOADED);
		}

		KeywordRecord keywordRecord = KeywordRecord.builder()
			.coupleKeyword(coupleKeyword)
			.user(user)
			.date(today)
			.imageKey(request.imageKey())
			.build();
		keywordRecordRepository.save(keywordRecord);
	}

	private TodayKeywordResponse.MemberInfo getMemberInfo(User user, KeywordRecord keywordRecord) {
		String profileImageUrl = null;
		String questionImageUrl = null;
		LocalDateTime answeredAt = null;
		if (user.getProfileImageKey() != null) {
			profileImageUrl = s3Service.createGetPresignedUrl(user.getProfileImageKey());
		}
		if (keywordRecord != null) {
			questionImageUrl = s3Service.createGetPresignedUrl(keywordRecord.getImageKey());
			answeredAt = keywordRecord.getCreatedAt();
		}

		return TodayKeywordResponse.MemberInfo.builder()
			.name(user.getNickname())
			.profileImageUrl(profileImageUrl)
			.questionImageUrl(questionImageUrl)
			.answeredAt(answeredAt)
			.build();
	}

	private String generateKeywordQuestion(Keyword keyword) {
		String content = keyword.getContent();
		return "오늘의 \"" + content + "\" 사진은?";
	}
}
