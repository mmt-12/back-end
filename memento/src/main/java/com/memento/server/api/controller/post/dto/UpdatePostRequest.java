package com.memento.server.api.controller.post.dto;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePostRequest(
	@Size(max = 510, message = "content는 최대 크기가 510입니다.")
	String content,

	List<Long> oldPictures
) {
}
