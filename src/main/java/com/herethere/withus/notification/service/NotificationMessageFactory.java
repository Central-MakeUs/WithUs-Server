package com.herethere.withus.notification.service;

import java.util.Map;

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
			case QUESTION_GENERATED -> questionGeneratedMessage();
			case QUESTION_ANSWERED -> questionAnsweredMessage(event);
			case KEYWORD_ANSWERED -> keywordAnsweredMessage(event);
		};
	}

	private NotificationMessage pokeMessage(CoupleNotificationEvent event) {
		String senderName = userService.getNickname(event.senderId());
		return new NotificationMessage(
			senderName + "님이 사진을 기다리고 있어요!",
			"지금 바로 사진을 보내볼까요?",
			null
		);
	}

	private NotificationMessage questionGeneratedMessage() {
		return new NotificationMessage(
			"오늘의 랜덤 질문이 도착했어요",
			"오늘의 질문에 답해볼까요?",
			Map.of("push", "/today_question")
		);
	}

	private NotificationMessage questionAnsweredMessage(CoupleNotificationEvent event) {
		String senderName = userService.getNickname(event.senderId());
		return new NotificationMessage(
			senderName + "님이 오늘의 질문에 답했어요!",
			"상대방의 답변을 확인해볼까요?",
			Map.of("push", "/today_question")
		);
	}

	private NotificationMessage keywordAnsweredMessage(CoupleNotificationEvent event) {
		String senderName = userService.getNickname(event.senderId());
		String coupleKeywordId = event.data().get("coupleKeywordId");
		return new NotificationMessage(
			senderName + "님이 사진을 보냈어요!",
			"사진이 도착했어요, 확인해볼까요?",
			coupleKeywordId != null ? Map.of("push", "/today_keyword/" + coupleKeywordId) : null
		);
	}
}
