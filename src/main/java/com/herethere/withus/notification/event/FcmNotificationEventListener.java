package com.herethere.withus.notification.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.herethere.withus.couple.service.CoupleService;
import com.herethere.withus.notification.dto.internal.CoupleNotificationEvent;
import com.herethere.withus.notification.dto.internal.NotificationMessage;
import com.herethere.withus.notification.service.FcmSendService;
import com.herethere.withus.notification.service.NotificationHistoryService;
import com.herethere.withus.notification.service.NotificationMessageFactory;
import com.herethere.withus.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FcmNotificationEventListener {

	private final FcmSendService fcmSendService;
	private final NotificationMessageFactory notificationMessageFactory;
	private final NotificationHistoryService notificationHistoryService;
	private final CoupleService coupleService;
	private final UserService userService;

	@Async("fcmExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleFcmEvent(CoupleNotificationEvent event) {
		List<Long> targetIds = new ArrayList<>();

		if (event.receiverId() != null) {
			targetIds.add(event.receiverId());
		} else {
			// receiverId가 null이면 커플 멤버 2명을 모두 후보로 올림
			// (예: senderId도 null, receiverId도 null인 상황 포함)
			targetIds = coupleService.getMemberIds(event.coupleId());
		}

		// 2. 후보들(targetIds)에 대해 개별 검증 후 발송
		for (Long receiverId : targetIds) {
			if (!coupleService.canSendNotification(event.coupleId(), event.senderId(), receiverId)) {
				continue;
			}

			if (!userService.isActive(receiverId)) {
				continue;
			}

			NotificationMessage message = notificationMessageFactory.create(event);

			Long coupleKeywordId = parseCoupleKeywordId(event.data());
			notificationHistoryService.save(receiverId, event.notificationType(),
				message.title(), message.content(), coupleKeywordId);

			fcmSendService.sendToUser(receiverId, message.title(), message.content(), message.data());
		}
	}

	private Long parseCoupleKeywordId(java.util.Map<String, String> data) {
		if (data == null) {
			return null;
		}
		String value = data.get("coupleKeywordId");
		if (value == null) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
