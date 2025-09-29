package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record BirthdayNotification(
	Long communityId,
	Long associateId
) implements NotificationEvent {

	public static BirthdayNotification from(Long communityId, Long associateId) {
		return BirthdayNotification.builder()
			.communityId(communityId)
			.associateId(associateId)
			.build();
	}
}
