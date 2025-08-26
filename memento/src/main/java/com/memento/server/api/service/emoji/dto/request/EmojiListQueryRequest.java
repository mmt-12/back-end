package com.memento.server.api.service.emoji.dto.request;

import lombok.Builder;

@Builder
public record EmojiListQueryRequest (
	Long communityId,
	Long cursor,
	int size,
	String keyword
){

	public static EmojiListQueryRequest of(Long communityId, Long cursor, int size, String keyword) {
		return new EmojiListQueryRequest(
			communityId,
			cursor,
			size,
			keyword
		);
	}
}
