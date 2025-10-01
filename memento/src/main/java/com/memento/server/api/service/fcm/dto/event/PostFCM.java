package com.memento.server.api.service.fcm.dto.event;

import lombok.Builder;

@Builder
public record PostFCM(
	Long makePostAssociateId,
	Long memoryId,
	Long postId
) implements FCMEvent {

	public static PostFCM of(Long makePostAssociateId, Long memoryId, Long postId) {
		return PostFCM.builder()
			.makePostAssociateId(makePostAssociateId)
			.memoryId(memoryId)
			.postId(postId)
			.build();
	}
}
