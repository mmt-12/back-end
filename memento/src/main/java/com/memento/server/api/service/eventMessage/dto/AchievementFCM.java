package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record AchievementFCM(
	Long receiverId
) implements FCMEvent {

	public static AchievementFCM of(Long receiverId) {
		return AchievementFCM.builder()
			.receiverId(receiverId)
			.build();
	}
}
