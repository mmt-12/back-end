package com.memento.server.api.controller.memory.dto.response;

import com.memento.server.domain.memory.Memory;

import lombok.Builder;

@Builder
public record CreateUpdateMemoryResponse(
	Long memoryId
) {

	public static CreateUpdateMemoryResponse from(Memory memory) {
		return CreateUpdateMemoryResponse.builder()
			.memoryId(memory.getId())
			.build();
	}
}
