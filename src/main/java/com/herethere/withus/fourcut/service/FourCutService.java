package com.herethere.withus.fourcut.service;

import static com.herethere.withus.common.exception.ErrorCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.dto.internal.CreatedAtIdCursor;
import com.herethere.withus.common.exception.ForbiddenException;
import com.herethere.withus.common.exception.NotFoundException;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.fourcut.domain.FourCut;
import com.herethere.withus.fourcut.dto.request.FourCutUploadRequest;
import com.herethere.withus.fourcut.dto.response.FourCutCursorResponse;
import com.herethere.withus.fourcut.repository.FourCutRepository;
import com.herethere.withus.s3.domain.ImageType;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FourCutService {
	private final FourCutRepository fourCutRepository;
	private final S3Service s3Service;
	private final AppContextService appContextService;
	private final CursorCodec cursorCodec;

	@Transactional(readOnly = true)
	public FourCutCursorResponse getFourCutsByCursor(int size, String cursor) {
		CreatedAtIdCursor payload = cursorCodec.decode(cursor, CreatedAtIdCursor.class);
		LocalDateTime createdAtCursor = payload == null ? null : payload.createdAt();
		Long idCursor = payload == null ? null : payload.id();

		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(user);
		Pageable pageable = PageRequest.of(0, size + 1);

		// 조회
		List<FourCut> results = fourCutRepository.findFourCutsByCursor(couple.getId(), createdAtCursor,
			idCursor, pageable);

		boolean hasNext = results.size() > size;
		// 실제 전달할 페이지
		List<FourCut> page = hasNext ? results.subList(0, size) : results;

		String nextCursor = null;
		if (hasNext) {
			FourCut last = page.getLast();
			nextCursor = cursorCodec.encode(new CreatedAtIdCursor(last.getCreatedAt(), last.getId()));
		}

		List<FourCutCursorResponse.FourCutInfo> fourCutInfos = page.stream().map(fc -> {
			String imageUrl = s3Service.createThumbnailImageUrl(fc.getImageKey());
			return new FourCutCursorResponse.FourCutInfo(fc.getId(), imageUrl, fc.getCreatedAt());
		}).toList();

		return new FourCutCursorResponse(fourCutInfos, nextCursor, hasNext);
	}

	@Transactional
	public void uploadFourCutImage(FourCutUploadRequest fourCutUploadRequest) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(user);
		String imageKey = fourCutUploadRequest.imageKey();

		String finalImageKey = s3Service.processImagePublish(imageKey, user.getId(), ImageType.MEMORY);

		FourCut fourCut = FourCut.builder()
			.couple(couple)
			.user(user)
			.imageKey(finalImageKey)
			.build();
		fourCutRepository.save(fourCut);
	}

	@Transactional
	public void deleteFourCut(Long fourCutId) {
		User user = appContextService.getCoupledUser();
		Couple couple = appContextService.getCouple(user);
		FourCut fourCut = fourCutRepository.findById(fourCutId)
			.orElseThrow(() -> new NotFoundException(FOUR_CUT_NOT_FOUND));

		if (!fourCut.getCouple().getId().equals(couple.getId())) {
			throw new ForbiddenException(FOUR_CUT_NOT_FOUND);
		}
		fourCutRepository.delete(fourCut);
	}
}
