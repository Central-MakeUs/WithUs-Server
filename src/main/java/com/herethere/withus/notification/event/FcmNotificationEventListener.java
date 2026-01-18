package com.herethere.withus.notification.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.herethere.withus.notification.dto.internal.FcmNotificationEvent;
import com.herethere.withus.notification.service.FcmSendService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FcmNotificationEventListener {

	private final FcmSendService fcmSendService;

	@Async("fcmExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleFcmEvent(FcmNotificationEvent event) {
		fcmSendService.sendToUser(event.userId(), event.title(), event.body(), event.data());
	}
}
