package com.memento.server.api.service.eventMessage.dto;

import com.memento.server.domain.memory.Memory;

import lombok.Builder;

@Builder
public record MemoryNotification(
	Long memoryId,
	Long communityId,
	Long authorId
) {
	public static MemoryNotification from(Long memoryId, Long communityId, Long authorId) {
		return MemoryNotification.builder()
			.memoryId(memoryId)
			.communityId(communityId)
			.authorId(authorId)
			.build();
	}
}
