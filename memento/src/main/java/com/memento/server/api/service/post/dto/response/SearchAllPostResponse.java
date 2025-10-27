package com.memento.server.api.service.post.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record SearchAllPostResponse(
	Long nextCursor,
	boolean hasNext,
	List<SearchPostResponse> posts
) {
}
