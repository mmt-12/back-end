package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record MbtiNotification(
	Long associateId
) implements NotificationEvent {

	public static MbtiNotification from(Long associateId) {
		return MbtiNotification.builder()
			.associateId(associateId)
			.build();
	}
}
