package com.herethere.withus.couple.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.domain.CoupleStatus;
import com.herethere.withus.couple.dto.request.CoupleInitializeRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.repository.KeywordRepository;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.InviteCodeRepository;
import com.herethere.withus.user.repository.UserRepository;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleService {
	private final UserContextService userContextService;
	private final CoupleRepository coupleRepository;
	private final InviteCodeRepository inviteCodeRepository;
	private final UserRepository userRepository;
	private final KeywordRepository keywordRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;

	@Transactional(readOnly = true)
	public CoupleJoinPreviewResponse checkCoupleJoinPreview(CoupleJoinPreviewRequest request) {
		InviteCode inviteCode = getInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = userContextService.getCurrentUser();

		if (sender.getId().equals(receiver.getId())) {
			throw new ConflictException(INVITED_SAME_USER);
		}

		if (sender.getCouple() != null || receiver.getCouple() != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		return new CoupleJoinPreviewResponse(sender.getNickname(), receiver.getNickname(), request.inviteCode());
	}

	@Transactional
	public CoupleJoinResponse joinCouple(CoupleJoinRequest request) {
		InviteCode inviteCode = getInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = userContextService.getCurrentUser();

		if (sender.getId().equals(receiver.getId())) {
			throw new ConflictException(INVITED_SAME_USER);
		}

		if (sender.getCouple() != null || receiver.getCouple() != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		Couple couple = coupleRepository.save(Couple.create(sender, receiver));

		inviteCodeRepository.delete(inviteCode);
		inviteCodeRepository.deleteByUser(receiver);

		return new CoupleJoinResponse(couple.getId());
	}

	@Transactional
	public void initializeCoupleSettings(CoupleInitializeRequest request) {
		User user = userContextService.getCurrentUser();
		Couple couple = user.getCouple();

		if (couple == null) {
			throw new NotFoundException(COUPLE_NOT_FOUND);
		}
		if (couple.getStatus() == CoupleStatus.ACTIVE) {
			throw new ConflictException(COUPLE_ALREADY_INITIALIZED);
		}
		if (couple.getStatus() == CoupleStatus.DELETED) {
			throw new ConflictException(COUPLE_DELETED);
		}

		Set<Keyword> finalKeywords = new HashSet<>();
		finalKeywords.addAll(keywordRepository.findAllById(request.defaultKeywordIds()));

		// 커스텀 키워드 저장
		List<String> customKeywords = request.customKeywords();
		for (String keywordContent : customKeywords) {
			String trimmedKeyword = keywordContent.trim();
			if (trimmedKeyword.isEmpty()) continue;
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
			finalKeywords.add(keyword);
		}

		List<CoupleKeyword> coupleKeywords = finalKeywords.stream()
			.map(k -> CoupleKeyword.builder()
				.keyword(k)
				.couple(couple)
				.build())
			.toList();

		coupleKeywordRepository.saveAll(coupleKeywords);

		couple.initialize(request.questionTime());
	}

	private InviteCode getInviteCode(String code) {
		return inviteCodeRepository.findByCode(code).orElseThrow(() -> new NotFoundException(CODE_NOT_FOUND));
	}
}
