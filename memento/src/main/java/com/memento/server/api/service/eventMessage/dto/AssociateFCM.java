package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record AssociateFCM(
	String nickname,
	Long communityId,
	Long associateId
) implements FCMEvent {

	public static AssociateFCM from(String nickname, Long communityId, Long associateId) {
		return AssociateFCM.builder()
			.nickname(nickname)
			.communityId(communityId)
			.associateId(associateId)
			.build();
	}
}
