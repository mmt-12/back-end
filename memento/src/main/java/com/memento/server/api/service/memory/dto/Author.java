package com.memento.server.api.service.memory.dto;

import lombok.Builder;

@Builder
public record Author(
	Long id,
	String imageUrl,
	String nickname,
	Achievement achievement
) {
	@Builder
	public record Achievement(
		Long id,
		String name
	) {
	}
}
