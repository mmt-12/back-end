package com.memento.server.api.controller.post.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record ReadAllPostResponse(
	Long cursor,
	boolean hasNext,
	List<ReadPostResponse> posts
) {
	public static ReadAllPostResponse from() {
		return ReadAllPostResponse.builder()
			.cursor(3L)
			.hasNext(false)
			.posts(List.of(ReadPostResponse.from()))
			.build();
	}
}
