package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record BirthdayFCM(
	Long communityId,
	Long birthdayAssociateId
) implements FCMEvent {

	public static BirthdayFCM from(Long communityId, Long birthdayAssociateId) {
		return BirthdayFCM.builder()
			.communityId(communityId)
			.birthdayAssociateId(birthdayAssociateId)
			.build();
	}
}
