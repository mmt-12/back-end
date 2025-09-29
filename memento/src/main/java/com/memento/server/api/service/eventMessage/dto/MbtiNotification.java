package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record MbtiNotification(
	Long receiverId
) implements NotificationEvent {

	public static MbtiNotification from(Long receiverId) {
		return MbtiNotification.builder()
			.receiverId(receiverId)
			.build();
	}
}
