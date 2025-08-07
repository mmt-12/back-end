package com.memento.server.api.service.emoji.dto.request;

import lombok.Builder;

@Builder
public record EmojiRemoveRequest(
	Long associateId,
	Long emojiId
) {

	public static EmojiRemoveRequest of(Long associateId, Long emojiId) {
		return EmojiRemoveRequest.builder().associateId(associateId).emojiId(emojiId).build();
	}
}
