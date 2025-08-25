package com.memento.server.api.controller.post.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreatePostRequest(
	@Size(max = 510, message = "content는 최대 크기가 510입니다.")
	String content
) {
}
