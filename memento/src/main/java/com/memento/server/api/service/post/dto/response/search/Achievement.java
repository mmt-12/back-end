package com.memento.server.api.service.post.dto.response.search;

import lombok.Builder;

@Builder
public record Achievement(
	Long id,
	String name
) {
}
