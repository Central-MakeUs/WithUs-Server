package com.herethere.withus.notification.dto.internal;

import java.util.Map;

public record NotificationMessage(
	String title, String content, Map<String, String> data
) {
}
