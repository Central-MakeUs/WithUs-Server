package com.herethere.withus.archive.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.herethere.withus.archive.dto.internal.ArchiveDayDto;
import com.herethere.withus.archive.dto.response.ArchiveListResponse;
import com.herethere.withus.archive.repository.ArchiveRepository;
import com.herethere.withus.common.dto.internal.DateCursor;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.couple.domain.Couple;
import com.herethere.withus.s3.service.S3Service;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.service.UserContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArchiveService {

	private final UserContextService userContextService;
	private final ArchiveRepository archiveRepository;
	private final S3Service s3Service;
	private final CursorCodec cursorCodec;

	public ArchiveListResponse getArchivesByCursor(String cursor, int size) {
		DateCursor dateCursor = cursor == null ? null : cursorCodec.decode(cursor, DateCursor.class);
		LocalDate date = dateCursor == null ? null : dateCursor.date();
		User user = userContextService.getCurrentUser();
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
}

