package com.memento.server.api.service.eventMessage.dto;

import lombok.Builder;

@Builder
public record MemoryFCM(
	Long memoryId,
	Long makeMemoryAssociateId
) implements FCMEvent {

	public static MemoryFCM from(Long memoryId, Long makeMemoryAssociateId) {
		return MemoryFCM.builder()
			.memoryId(memoryId)
			.makeMemoryAssociateId(makeMemoryAssociateId)
			.build();
	}
}
