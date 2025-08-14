package com.memento.server.api.controller.memory.dto;

import com.memento.server.domain.memory.Memory;

import lombok.Builder;

@Builder
public record CreateMemoryResponse(
	Long memoryId
) {
	
	public static CreateMemoryResponse from(Memory memory) {
		return CreateMemoryResponse.builder()
			.memoryId(memory.getId())
			.build();
	}
}
