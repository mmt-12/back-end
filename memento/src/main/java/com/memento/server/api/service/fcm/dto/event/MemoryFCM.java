package com.memento.server.api.service.fcm.dto.event;

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
