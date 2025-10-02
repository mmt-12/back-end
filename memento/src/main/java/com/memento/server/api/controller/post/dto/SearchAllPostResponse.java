package com.memento.server.api.controller.post.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record SearchAllPostResponse(
	Long nextCursor,
	boolean hasNext,
	List<SearchPostResponse> posts
) {
}
