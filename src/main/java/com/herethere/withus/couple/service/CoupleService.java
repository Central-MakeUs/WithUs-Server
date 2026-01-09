package com.herethere.withus.couple.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.exception.ConflictException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.couple.domain.CoupleStatus;
import com.herethere.withus.couple.dto.request.CoupleJoinPreviewRequest;
import com.herethere.withus.couple.dto.request.CoupleJoinRequest;
import com.herethere.withus.couple.dto.response.CoupleJoinPreviewResponse;
import com.herethere.withus.couple.dto.response.CoupleJoinResponse;
import com.herethere.withus.couple.repository.CoupleRepository;
import com.herethere.withus.user.domain.CodeStatus;
import com.herethere.withus.user.domain.InviteCode;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.InviteCodeRepository;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupleService {
	private final UserContextService userContextService;
	private final CoupleRepository coupleRepository;
	private final InviteCodeRepository inviteCodeRepository;

	@Transactional(readOnly = true)
	public CoupleJoinPreviewResponse checkCoupleJoinPreview(CoupleJoinPreviewRequest request) {
		InviteCode inviteCode = getActiveInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = userContextService.getCurrentUser();

		if (sender.getCouple() != null || receiver.getCouple() != null) {
			throw new ConflictException(COUPLE_ALREADY_EXISTS);
		}

		return new CoupleJoinPreviewResponse(sender.getNickname(), receiver.getNickname(), request.inviteCode());
	}

	@Transactional
	public CoupleJoinResponse joinCouple(CoupleJoinRequest request) {
		InviteCode inviteCode = getActiveInviteCode(request.inviteCode());

		User sender = inviteCode.getUser();
		User receiver = userContextService.getCurrentUser();

		if (sender.getId().equals(receiver.getId())) {
			throw new ConflictException(INVITED_SAME_USER);
		}

		Couple couple = coupleRepository.save(Couple.builder().status(CoupleStatus.ACTIVE).build());

		sender.assignCouple(couple);
		receiver.assignCouple(couple);

		inviteCode.useCode();
		inviteCodeRepository.findByUserAndStatus(receiver, CodeStatus.ACTIVE).ifPresent(InviteCode::useCode);

		return new CoupleJoinResponse(couple.getId());
	}

	private InviteCode getActiveInviteCode(String code) {
		return inviteCodeRepository.findByCodeAndStatus(code, CodeStatus.ACTIVE)
			.orElseThrow(() -> new NotFoundException(CODE_NOT_FOUND));
	}
}
