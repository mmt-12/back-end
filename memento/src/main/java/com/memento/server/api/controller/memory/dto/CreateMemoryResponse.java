package com.memento.server.api.controller.memory.dto;

import lombok.Builder;

@Builder
public record CreateMemoryResponse(
	Long memoryId
) {
}
