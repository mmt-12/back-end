package com.memento.server.api.controller.post.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record SearchAllPostResponse(
	Long cursor,
	boolean hasNext,
	List<SearchPostResponse> posts
) {
}
