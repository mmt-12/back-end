package com.memento.server.api.service.eventMessage.dto;

import com.memento.server.domain.memory.Memory;

import lombok.Builder;

@Builder
public record MemoryNotification(
	Memory memory
) {
	public static MemoryNotification from(Memory memory) {
		return MemoryNotification.builder()
			.memory(memory)
			.build();
	}
}
