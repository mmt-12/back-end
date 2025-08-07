package com.memento.server.api.service.emoji.dto.request;

import lombok.Builder;

@Builder
public record EmojiRemoveRequest(
	Long communityId,
	Long emojiId
) {

	public static EmojiRemoveRequest of(Long communityId, Long emojiId) {
		return EmojiRemoveRequest.builder().communityId(communityId).emojiId(emojiId).build();
	}
}
