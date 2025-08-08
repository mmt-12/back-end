package com.memento.server.api.service.emoji.dto.request;

import lombok.Builder;

@Builder
public record EmojiListQueryRequest (
	Long groupId,
	Long cursor,
	int size,
	String keyword
){

	public static EmojiListQueryRequest of(Long groupId, Long cursor, int size, String keyword) {
		return new EmojiListQueryRequest(
			groupId,
			cursor,
			size <= 0 ? 10 : size,
			keyword != null && !keyword.isBlank() ? keyword.trim() : null
		);
	}
}
