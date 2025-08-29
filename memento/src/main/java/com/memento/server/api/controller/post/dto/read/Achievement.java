package com.memento.server.api.controller.post.dto.read;

import lombok.Builder;

@Builder
public record Achievement(
	Long id,
	String name
) {
}
