package com.memento.server.api.service.comment.dto.request;

import lombok.Builder;

@Builder
public record EmojiCommentCreateServiceRequest(
	Long emojiId,
	Long postId,
	Long associateId
) {

	public static EmojiCommentCreateServiceRequest of(Long emojiId, Long postId, Long associateId) {
		return EmojiCommentCreateServiceRequest.builder()
			.emojiId(emojiId)
			.postId(postId)
			.associateId(associateId)
			.build();
	}
}
