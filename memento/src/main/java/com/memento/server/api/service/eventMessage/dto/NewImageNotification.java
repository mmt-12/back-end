package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record NewImageNotification(
	Long associateId
) implements NotificationEvent {

	public static NewImageNotification from(Long associateId) {
		return NewImageNotification.builder()
			.associateId(associateId)
			.build();
	}
}
