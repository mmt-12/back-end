package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record AssociateNotification(
	Long communityId,
	Long associateId
) implements NotificationEvent {

	public static AssociateNotification from(Long communityId, Long associateId) {
		return AssociateNotification.builder()
			.communityId(communityId)
			.associateId(associateId)
			.build();
	}
}
