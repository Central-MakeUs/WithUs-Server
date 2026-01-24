package com.herethere.withus.fourcut.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.annotation.RequiresActiveCouple;
import com.herethere.withus.common.dto.internal.CursorPayload;
import com.herethere.withus.common.exception.BadRequestException;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.fourcut.domain.FourCut;
import com.herethere.withus.fourcut.dto.request.FourCutUploadRequest;
import com.herethere.withus.fourcut.dto.response.FourCutCursorResponse;
import com.herethere.withus.fourcut.repository.FourCutRepository;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FourCutService {
	private final FourCutRepository fourCutRepository;
	private final UserContextService userContextService;
	private final S3Service s3Service;
	private final CursorCodec cursorCodec;

	@Transactional(readOnly = true)
	public FourCutCursorResponse getFourCutsByCursor(int size, String cursor) {
		CursorPayload payload = cursorCodec.decode(cursor);
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		Pageable pageable = PageRequest.of(0, size + 1);

		// 조회
		List<FourCut> results = fourCutRepository.findFourCutsByCursor(couple.getId(), payload.createdAt(),
			payload.id(), pageable);

		boolean hasNext = results.size() > size;
		// 실제 전달할 페이지
		List<FourCut> page = hasNext ? results.subList(0, size) : results;

		String nextCursor = null;
		if (hasNext) {
			FourCut last = page.getLast();
			nextCursor = cursorCodec.encode(last.getCreatedAt(), last.getId());
		}

		List<FourCutCursorResponse.FourCutInfo> fourCutInfos = page.stream().map(fc -> {
			String imageUrl = s3Service.createGetPresignedUrl(fc.getImageKey());
			return new FourCutCursorResponse.FourCutInfo(fc.getId(), imageUrl, fc.getCreatedAt());
		}).toList();

		return new FourCutCursorResponse(fourCutInfos, nextCursor, hasNext);
	}

	@Transactional
	@RequiresActiveCouple
	public void uploadFourCutImage(FourCutUploadRequest fourCutUploadRequest) {
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		String imageKey = fourCutUploadRequest.imageKey();

		if (imageKey == null || imageKey.isBlank()) {
			throw new BadRequestException(NEED_IMAGE_KEY);
		}

		if (!imageKey.endsWith(".jpg")) {
			throw new BadRequestException(WRONG_IMAGE_FORMAT);
		}

		String expectedPrefix = "users/" + user.getId() + "/four-cut/";
		if (!imageKey.startsWith(expectedPrefix)) {
			throw new ForbiddenException(WRONG_IMAGE_KEY);
		}

		FourCut fourCut = FourCut.builder()
			.couple(couple)
			.user(user)
			.imageKey(imageKey)
			.build();
		fourCutRepository.save(fourCut);
	}

	@Transactional
	@RequiresActiveCouple
	public void deleteFourCut(Long fourCutId) {
		User user = userContextService.getCoupledUser();
		Couple couple = user.getCouple();
		FourCut fourCut = fourCutRepository.findById(fourCutId)
			.orElseThrow(() -> new NotFoundException(FOUR_CUT_NOT_FOUND));

		if (!fourCut.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(FOUR_CUT_NOT_FOUND);
		}
		fourCutRepository.delete(fourCut);
	}
}
