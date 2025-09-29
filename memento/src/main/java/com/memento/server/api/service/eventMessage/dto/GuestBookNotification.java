package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record GuestBookNotification(
	Long receiverId
) implements NotificationEvent {

	public static GuestBookNotification from(Long receiverId) {
		return GuestBookNotification.builder()
			.receiverId(receiverId)
			.build();
	}
}
