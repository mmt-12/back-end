package com.memento.server.api.service.emoji.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record EmojiListResponse(
	List<EmojiResponse> emojis,
	Long nextCursor,
	boolean hasNext
) {

	public static EmojiListResponse of(List<EmojiResponse> emojis, Long nextCursor, boolean hasNext) {
		return EmojiListResponse.builder()
			.emojis(emojis)
			.nextCursor(nextCursor)
			.hasNext(hasNext)
			.build();
	}
}
