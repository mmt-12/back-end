package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record AchievementNotification(
	Long receiverId
) implements NotificationEvent {

	public static AchievementNotification from(Long receiverId) {
		return AchievementNotification.builder()
			.receiverId(receiverId)
			.build();
	}
}
