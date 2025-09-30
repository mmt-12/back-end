package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record ReactionFCM(
	String actorNickname,
	Long actorId,
	Long memoryId,
	Long postId,
	Long receiverId
) implements FCMEvent {

	public static ReactionFCM of(String actorNickname, Long actorId, Long memoryId, Long postId, Long receiverId) {
		return ReactionFCM.builder()
			.actorNickname(actorNickname)
			.actorId(actorId)
			.memoryId(memoryId)
			.postId(postId)
			.receiverId(receiverId)
			.build();
	}
}
