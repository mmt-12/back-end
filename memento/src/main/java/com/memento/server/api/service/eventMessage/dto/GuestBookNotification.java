package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record GuestBookNotification(
	Long associateId
) implements NotificationEvent {

	public static GuestBookNotification from(Long associateId) {
		return GuestBookNotification.builder()
			.associateId(associateId)
			.build();
	}
}
