package com.herethere.withus.notification.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.herethere.withus.common.dto.internal.CreatedAtIdCursor;
import com.herethere.withus.common.util.CursorCodec;
import com.herethere.withus.notification.domain.NotificationHistory;
import com.herethere.withus.notification.domain.NotificationType;
import com.herethere.withus.notification.dto.response.NotificationCursorResponse;
import com.herethere.withus.notification.repository.NotificationHistoryRepository;
import com.herethere.withus.user.domain.User;
import com.herethere.withus.user.repository.UserRepository;
import com.herethere.withus.user.service.AppContextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationHistoryService {

	private final NotificationHistoryRepository notificationHistoryRepository;
	private final UserRepository userRepository;
	private final AppContextService appContextService;
	private final CursorCodec cursorCodec;

	@Transactional
	public void save(Long userId, NotificationType type, String title, String content, Long coupleKeywordId) {
		User user = userRepository.getReferenceById(userId);
		NotificationHistory history = NotificationHistory.builder()
			.user(user)
			.type(type)
			.title(title)
			.content(content)
			.coupleKeywordId(coupleKeywordId)
			.build();
		notificationHistoryRepository.save(history);
	}

	@Transactional(readOnly = true)
	public NotificationCursorResponse getNotificationHistory(int size, String cursor) {
		User user = appContextService.getInitializedAndActiveUser();
		CreatedAtIdCursor payload = cursorCodec.decode(cursor, CreatedAtIdCursor.class);
		Pageable pageable = PageRequest.of(0, size + 1);

		List<NotificationHistory> results;
		if (payload == null) {
			results = notificationHistoryRepository.findByUserId(user.getId(), pageable);
		} else {
			results = notificationHistoryRepository.findByUserIdWithCursor(
				user.getId(), payload.createdAt(), payload.id(), pageable);
		}

		boolean hasNext = results.size() > size;
		List<NotificationHistory> page = hasNext ? results.subList(0, size) : results;

		String nextCursor = null;
		if (hasNext) {
			NotificationHistory last = page.getLast();
			nextCursor = cursorCodec.encode(new CreatedAtIdCursor(last.getCreatedAt(), last.getId()));
		}

		List<NotificationCursorResponse.NotificationInfo> notifications = page.stream()
			.map(n -> new NotificationCursorResponse.NotificationInfo(n.getTitle(), n.getContent(), n.getPush()))
			.toList();

		return new NotificationCursorResponse(notifications, nextCursor, hasNext);
	}
}
