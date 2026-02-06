package com.herethere.withus.notification.service;

import org.springframework.stereotype.Component;

import com.herethere.withus.notification.dto.internal.CoupleNotificationEvent;
import com.herethere.withus.notification.dto.internal.NotificationMessage;
import com.herethere.withus.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationMessageFactory {

	private final UserService userService;

	public NotificationMessage create(CoupleNotificationEvent event) {
		return switch (event.notificationType()) {
			case POKE -> pokeMessage(event);
			case PHOTO_UPLOADED -> photoUploadedMessage(event);
			case QUESTION_GENERATED -> questionGeneratedMessage(event);
		};
	}

	private NotificationMessage pokeMessage(CoupleNotificationEvent event) {
		String senderName = userService.getNickname(event.senderId());
		return new NotificationMessage(
			senderName + "님이 사진을 기다리고 있어요!",
			"지금 바로 사진을 보내볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}

	private NotificationMessage photoUploadedMessage(CoupleNotificationEvent event) {
		String senderName = userService.getNickname(event.senderId());
		return new NotificationMessage(
			senderName + "님이 사진을 보냈어요!",
			"사진이 도착했어요, 확인해볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}

	private NotificationMessage questionGeneratedMessage(CoupleNotificationEvent event) {
		return new NotificationMessage(
			"오늘의 랜덤 질문이 도착했어요",
			"오늘의 질문에 답해볼까요?",
			null //TODO: 나중에 이동할 화면 정보 추가
		);
	}
}
