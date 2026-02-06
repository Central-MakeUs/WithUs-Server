package com.herethere.withus.notification.dto.internal;

import java.util.Map;
import java.util.Objects;

import com.herethere.withus.notification.domain.NotificationType;

public record CoupleNotificationEvent(Long coupleId, Long senderId, Long receiverId, NotificationType notificationType,
									  Map<String, String> data) {
	public static CoupleNotificationEvent of(Long coupleId, Long senderId, Long receiverId,
		NotificationType notificationType, Map<String, String> data) {
		return new CoupleNotificationEvent(
			coupleId,
			senderId,
			receiverId,
			notificationType,
			data != null ? data : Map.of()
		);
	}

	public static CoupleNotificationEvent toPartner(
		Long coupleId, Long senderId, Long receiverId, NotificationType type, Map<String, String> data) {
		Objects.requireNonNull(coupleId, "coupleId는 필수입니다.");
		Objects.requireNonNull(senderId, "senderId는 필수입니다.");
		Objects.requireNonNull(receiverId, "receiverId는 필수입니다");
		Objects.requireNonNull(type, "notificationType은 필수입니다.");

		if (senderId.equals(receiverId)) {
			throw new IllegalArgumentException(
				"Invalid CoupleNotificationEvent: senderId and receiverId must be different");
		}

		return new CoupleNotificationEvent(
			coupleId,
			senderId,
			receiverId,
			type,
			data
		);
	}

	public static CoupleNotificationEvent toBoth(
		Long coupleId, NotificationType type, Map<String, String> data
	) {
		Objects.requireNonNull(coupleId, "coupleId는 필수입니다.");
		Objects.requireNonNull(type, "notificationType은 필수입니다.");

		return new CoupleNotificationEvent(
			coupleId,
			null,
			null,
			type,
			data
		);
	}

}
