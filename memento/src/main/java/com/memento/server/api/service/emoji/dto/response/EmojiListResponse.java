package com.memento.server.api.service.emoji.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record EmojiListResponse(
	List<EmojiResponse> emoji,
	Long cursor,
	int size,
	Long nextCursor,
	boolean hasNext
) {

	public static EmojiListResponse of(List<EmojiResponse> emoji, Long cursor, int size, Long nextCursor,
		boolean hasNext) {
		return EmojiListResponse.builder()
			.emoji(emoji)
			.cursor(cursor)
			.size(size)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
