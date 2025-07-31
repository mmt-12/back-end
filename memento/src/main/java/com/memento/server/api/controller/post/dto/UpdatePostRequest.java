package com.memento.server.api.controller.post.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record UpdatePostRequest(
	String content,
	List<Long> oldPictures
) {
}
