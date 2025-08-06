package com.memento.server.api.controller.post.dto;

import lombok.Builder;

@Builder
public record CreatePostRequest(
	String content
) {
}
