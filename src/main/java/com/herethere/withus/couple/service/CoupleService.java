package com.herethere.withus.couple.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleKeyword;
import com.herethere.withus.couple.domain.CoupleKeywordStatus;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.request.SetCoupleKeywordRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;
import com.herethere.withus.couple.repository.CoupleKeywordRepository;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.keyword.domain.Keyword;
import com.herethere.withus.keyword.repository.KeywordRepository;
import com.herethere.withus.keyword.service.KeywordService;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.InviteCodeRepository;
import com.herethere.withus.user.repository.UserRepository;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleService {
	private final AppContextService appContextService;
	private final KeywordService keywordService;
	private final CoupleRepository coupleRepository;
	private final InviteCodeRepository inviteCodeRepository;
	private final UserRepository userRepository;
	private final KeywordRepository keywordRepository;
	private final CoupleKeywordRepository coupleKeywordRepository;

	@Transactional(readOnly = true)
	public CoupleJoinPreviewResponse checkCoupleJoinPreview(CoupleJoinPreviewRequest request) {
		InviteCode inviteCode = getInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = appContextService.getInitializedAndActiveUser();

		if (sender.getId().equals(receiver.getId())) {
			throw new ConflictException(INVITED_SAME_USER);
		}

		if (appContextService.findActiveCouple(sender).isPresent() || appContextService.findActiveCouple(receiver)
			.isPresent()) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		return new CoupleJoinPreviewResponse(sender.getNickname(), receiver.getNickname(), request.inviteCode());
	}

	@Transactional
	public CoupleJoinResponse joinCouple(CoupleJoinRequest request) {
		InviteCode inviteCode = getInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = appContextService.getInitializedAndActiveUser();

		if (sender.getId().equals(receiver.getId())) {
			throw new ConflictException(INVITED_SAME_USER);
		}

		if (appContextService.findActiveCouple(sender).isPresent() || appContextService.findActiveCouple(receiver)
			.isPresent()) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		Couple couple = coupleRepository.save(Couple.create(sender, receiver));

		inviteCodeRepository.delete(inviteCode);
		inviteCodeRepository.deleteByUser(receiver);

		return new CoupleJoinResponse(couple.getId());
	}

	@Transactional
	public void setCoupleKeywords(SetCoupleKeywordRequest request) {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);

		validateKeywordSize(request);

		List<CoupleKeyword> oldCoupleKeywords = coupleKeywordRepository.findAllByCoupleAndStatus(couple,
			CoupleKeywordStatus.ACTIVE);
		oldCoupleKeywords.forEach(CoupleKeyword::delete);

		Set<Keyword> finalKeywords = keywordService.getChosenKeywords(request.defaultKeywordIds(),
			request.customKeywords());

		for (Keyword keyword : finalKeywords) {
			coupleKeywordRepository.findByCoupleAndKeyword(couple, keyword)
				.ifPresentOrElse(
					CoupleKeyword::activate,
					() -> {
						CoupleKeyword newCoupleKeyword = CoupleKeyword.builder()
							.couple(couple)
							.keyword(keyword)
							.status(CoupleKeywordStatus.ACTIVE)
							.build();
						coupleKeywordRepository.save(newCoupleKeyword);
					}
				);
		}
	}

	@Transactional
	public void terminateCouple() {
		User user = appContextService.getInitializedAndActiveUser();
		Couple couple = appContextService.getActiveCoupleRequired(user);
		couple.deleteUser(user);
	}

	private InviteCode getInviteCode(String code) {
		return inviteCodeRepository.findByCode(code).orElseThrow(() -> new NotFoundException(CODE_NOT_FOUND));
	}

	private void validateKeywordSize(SetCoupleKeywordRequest request) {
		int totalSize = request.defaultKeywordIds().size() + request.customKeywords().size();
		if (totalSize < 1 || totalSize > 3) {
			throw new BadRequestException(INVALID_INPUT);
		}
	}
}
